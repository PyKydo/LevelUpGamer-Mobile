package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Usuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class AuthRepository : InAuthRepository {
    private data class UsuarioRegistro(val usuario: Usuario, val contrasena: String)

    private val usuariosRegistrados = mutableMapOf<String, UsuarioRegistro>()
    private var nextId: Long = 1

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    override val usuarioActual = _usuarioActual.asStateFlow()

    override suspend fun registrar(
        nombre: String,
        email: String,
        contrasena: String,
        fechaNacimiento: String
    ): Usuario {
        delay(250)

        val emailNormalizado = email.trim().lowercase()
        if (usuariosRegistrados.containsKey(emailNormalizado)) {
            throw IllegalArgumentException("El email ya está registrado.")
        }

        val nuevoUsuario = Usuario(
            id = nextId++,
            nombre = nombre.trim(),
            email = emailNormalizado,
            fechaNacimiento = fechaNacimiento.trim(),
            fotoPerfilUrl = null
        )

        usuariosRegistrados[emailNormalizado] = UsuarioRegistro(nuevoUsuario, contrasena)
        _usuarioActual.value = nuevoUsuario
        return nuevoUsuario
    }

    override suspend fun iniciarSesion(email: String, contrasena: String): Usuario {
        delay(200)
        val emailNormalizado = email.trim().lowercase()
        val registro = usuariosRegistrados[emailNormalizado]
            ?: throw IllegalArgumentException("El email no está registrado.")

        if (registro.contrasena != contrasena) {
            throw IllegalArgumentException("Credenciales inválidas.")
        }

        _usuarioActual.value = registro.usuario
        return registro.usuario
    }

    override suspend fun existe(email: String): Boolean {
        val emailNormalizado = email.trim().lowercase()
        return usuariosRegistrados.containsKey(emailNormalizado)
    }

    override suspend fun cerrarSesion() {
        _usuarioActual.value = null
    }

    override suspend fun actualizarPerfil(nombre: String, email: String) {
        val actual = _usuarioActual.value
            ?: throw IllegalStateException("No hay un usuario autenticado para actualizar.")

        val nombreNormalizado = nombre.trim()
        val emailNormalizado = email.trim().lowercase()

        if (nombreNormalizado.isBlank() || emailNormalizado.isBlank()) {
            throw IllegalArgumentException("Nombre y email no pueden estar vacíos.")
        }

        val registroActual = usuariosRegistrados[actual.email]
            ?: throw IllegalStateException("El usuario actual no existe en el registro interno.")

        if (emailNormalizado != actual.email && usuariosRegistrados.containsKey(emailNormalizado)) {
            throw IllegalArgumentException("El email ya está registrado.")
        }

        if (emailNormalizado != actual.email) {
            usuariosRegistrados.remove(actual.email)
        }

        val usuarioActualizado = registroActual.usuario.copy(
            nombre = nombreNormalizado,
            email = emailNormalizado
        )

        usuariosRegistrados[emailNormalizado] = UsuarioRegistro(usuarioActualizado, registroActual.contrasena)
        _usuarioActual.value = usuarioActualizado
    }
}