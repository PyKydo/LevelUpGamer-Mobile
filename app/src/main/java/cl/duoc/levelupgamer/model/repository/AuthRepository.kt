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
}