package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.auth.ChangePasswordRequest
import cl.duoc.levelupgamer.data.remote.dto.auth.LoginRequest
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRegistroDto
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioUpdateDto
import cl.duoc.levelupgamer.data.remote.mapper.toDomain
import cl.duoc.levelupgamer.data.session.TokenSession
import cl.duoc.levelupgamer.data.session.TokenStore
import cl.duoc.levelupgamer.model.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.json.JSONException
import org.json.JSONObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UsuarioRepository(
    private val authApi: LevelUpApi,
    private val secureApi: LevelUpApi,
    private val tokenStore: TokenStore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : InAuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    override val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()
    val sesionActiva: StateFlow<Boolean> = tokenStore.sessionFlow
        .map { it.hasValidTokens() }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = tokenStore.currentSession().hasValidTokens()
        )

    init {
        scope.launch { restoreSessionIfPossible() }
    }

    override suspend fun registrar(
        run: String,
        nombre: String,
        apellidos: String,
        correo: String,
        contrasena: String,
        fechaNacimiento: String,
        region: String,
        comuna: String,
        direccion: String,
        codigoReferido: String?
    ): Usuario = withContext(ioDispatcher) {
        val dto = authApi.register(
            UsuarioRegistroDto(
                run = run,
                nombre = nombre,
                apellidos = apellidos,
                correo = correo,
                contrasena = contrasena,
                fechaNacimiento = fechaNacimiento,
                region = region,
                comuna = comuna,
                direccion = direccion,
                codigoReferido = codigoReferido
            )
        )
        dto.toDomain()
    }

    override suspend fun iniciarSesion(email: String, contrasena: String): Usuario = withContext(ioDispatcher) {
        val response = authApi.login(LoginRequest(correo = email, contrasena = contrasena))
        val accessToken = response.accessToken ?: response.legacyToken
            ?: throw IllegalStateException("El backend no entregó token de acceso.")
        val refreshToken = response.refreshToken ?: throw IllegalStateException("El backend no entregó refresh token.")
        val userDto = response.usuario
        val resolvedRole = response.rol ?: userDto?.rol

        if (userDto != null) {
            val user = userDto.toDomain()
            tokenStore.persistSession(
                TokenSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = user.id,
                    email = user.email,
                    role = resolvedRole ?: user.rol
                )
            )
            _usuarioActual.value = user
            return@withContext user
        }

        val userId = response.usuarioId
            ?: throw IllegalStateException("El backend no envió información del usuario.")

        // Persist the tokens before hitting secured endpoints so the interceptor can attach them.
        val provisionalSession = TokenSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            role = resolvedRole
        )
        tokenStore.persistSession(provisionalSession)

        val fetchedUser = try {
            secureApi.getUser(userId).toDomain()
        } catch (t: Throwable) {
            tokenStore.clear()
            throw IllegalStateException("No se pudo obtener la información del usuario.", t)
        }

        tokenStore.persistSession(
            provisionalSession.copy(
                email = fetchedUser.email,
                role = resolvedRole ?: fetchedUser.rol
            )
        )
        _usuarioActual.value = fetchedUser
        fetchedUser
    }

    override suspend fun cerrarSesion() {
        tokenStore.clear()
        _usuarioActual.value = null
    }

    override suspend fun actualizarPerfil(nombre: String, email: String) = withContext(ioDispatcher) {
        val actual = _usuarioActual.value ?: throw IllegalStateException("No hay un usuario logueado")
        val updated = secureApi.updateUser(
            id = actual.id,
            body = UsuarioUpdateDto(
                nombre = nombre,
                apellidos = actual.apellido.orEmpty(),
                correo = email,
                region = actual.region,
                comuna = actual.comuna,
                direccion = actual.direccion,
                fechaNacimiento = actual.fechaNacimiento.ifBlank { null },
                telefono = actual.telefono,
                fotoPerfilUrl = actual.fotoPerfilUrl
            )
        ).toDomain()

        tokenStore.persistSession(
            tokenStore.currentSession().copy(email = updated.email)
        )
        _usuarioActual.value = updated
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) = withContext(ioDispatcher) {
        try {
            secureApi.changePassword(
                ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
            )
        } catch (e: HttpException) {
            val errBody = try { e.response()?.errorBody()?.string() } catch (_: Throwable) { null }
            if (errBody?.contains("No static resource") == true || e.code() == 404) {
                throw IllegalStateException("El servidor no soporta el endpoint de cambio de contraseña (api/auth/change-password).", e)
            }
            if (e.code() == 400) {
                val validationMessage = buildValidationMessage(errBody)
                if (!validationMessage.isNullOrBlank()) {
                    throw IllegalArgumentException(validationMessage, e)
                }
            }
            throw e
        }
    }

    suspend fun refreshPerfil() {
        restoreSessionIfPossible()
    }

    private suspend fun restoreSessionIfPossible() {
        val session = tokenStore.currentSession()
        val userId = session.userId ?: return
        try {
            val user = withContext(ioDispatcher) { secureApi.getUser(userId).toDomain() }
            _usuarioActual.value = user
        } catch (_: Exception) {
            // If we fail to fetch the user (network/server down, etc.) do NOT clear
            // the persisted session here — keep tokens so the user stays logged in
            // locally until they explicitly log out or the authenticator decides
            // the tokens are invalid (then it will clear them).
            _usuarioActual.value = null
        }
    }
}

private fun TokenSession.hasValidTokens(): Boolean =
    !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()

private fun buildValidationMessage(rawBody: String?): String? {
    if (rawBody.isNullOrBlank()) return null
    return try {
        val json = JSONObject(rawBody)
        val messages = mutableListOf<String>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.optString(key)
            if (!value.isNullOrBlank()) {
                messages += value
            }
        }
        messages.takeIf { it.isNotEmpty() }?.joinToString(separator = "\n")
    } catch (_: JSONException) {
        null
    }
}
