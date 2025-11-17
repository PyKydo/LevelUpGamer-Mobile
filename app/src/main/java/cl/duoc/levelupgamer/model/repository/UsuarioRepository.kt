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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioRepository(
    private val authApi: LevelUpApi,
    private val secureApi: LevelUpApi,
    private val tokenStore: TokenStore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : InAuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    override val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    init {
        scope.launch { restoreSessionIfPossible() }
    }

    override suspend fun registrar(
        nombre: String,
        run: String?,
        apellido: String?,
        email: String,
        contrasena: String,
        fechaNacimiento: String,
        telefono: String?,
        region: String?,
        comuna: String?,
        direccion: String?
    ): Usuario = withContext(ioDispatcher) {
        val dto = authApi.register(
            UsuarioRegistroDto(
                nombre = nombre,
                run = run,
                apellido = apellido,
                email = email,
                password = contrasena,
                fechaNacimiento = fechaNacimiento,
                telefono = telefono,
                region = region,
                comuna = comuna,
                direccion = direccion
            )
        )
        dto.toDomain()
    }

    override suspend fun iniciarSesion(email: String, contrasena: String): Usuario = withContext(ioDispatcher) {
        val response = authApi.login(LoginRequest(username = email, password = contrasena))
        val userDto = response.usuario ?: throw IllegalStateException("El backend no envió información del usuario.")
        val accessToken = response.accessToken ?: response.legacyToken
            ?: throw IllegalStateException("El backend no entregó token de acceso.")
        val refreshToken = response.refreshToken ?: throw IllegalStateException("El backend no entregó refresh token.")
        val user = userDto.toDomain()

        tokenStore.persistSession(
            TokenSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userId = user.id,
                email = user.email,
                role = response.rol ?: user.rol
            )
        )
        _usuarioActual.value = user
        user
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
                apellido = actual.apellido,
                email = email,
                fechaNacimiento = actual.fechaNacimiento.ifBlank { null },
                telefono = actual.telefono,
                direccion = actual.direccion,
                fotoPerfilUrl = actual.fotoPerfilUrl
            )
        ).toDomain()

        tokenStore.persistSession(
            tokenStore.currentSession().copy(email = updated.email)
        )
        _usuarioActual.value = updated
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) = withContext(ioDispatcher) {
        secureApi.changePassword(
            ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
        )
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
            tokenStore.clear()
            _usuarioActual.value = null
        }
    }
}