package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.local.dao.UsuarioDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioRepository(private val dao: UsuarioDao) : InAuthRepository {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    override val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    override suspend fun registrar(nombre: String, email: String, contrasena: String, fechaNacimiento: String): Usuario {
        if (existe(email)) {
            throw IllegalArgumentException("El email ya está registrado")
        }
        val usuario = Usuario(nombre = nombre, email = email, contrasena = contrasena, fechaNacimiento = fechaNacimiento)
        val id = dao.insertar(usuario)
        return usuario.copy(id = id)
    }

    override suspend fun iniciarSesion(email: String, contrasena: String): Usuario {
        val usuario = dao.obtenerPorEmail(email) ?: throw IllegalArgumentException("Usuario no encontrado")
        if (usuario.contrasena != contrasena) {
            throw IllegalArgumentException("Contraseña incorrecta")
        }
        _usuarioActual.value = usuario
        return usuario
    }

    override suspend fun existe(email: String): Boolean {
        return dao.obtenerPorEmail(email) != null
    }

    override suspend fun cerrarSesion() {
        _usuarioActual.value = null
    }

    override suspend fun actualizarPerfil(nombre: String, email: String) {
        val usuario = _usuarioActual.value ?: throw IllegalStateException("No hay un usuario logueado")
        if (usuario.email != email && existe(email)) {
            throw IllegalArgumentException("El nuevo email ya está en uso")
        }
        val usuarioActualizado = usuario.copy(nombre = nombre, email = email)
        dao.actualizar(usuarioActualizado)
        _usuarioActual.value = usuarioActualizado
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) {
        val usuario = _usuarioActual.value ?: throw IllegalStateException("No hay un usuario logueado")
        if (usuario.contrasena != currentPassword) {
            throw IllegalArgumentException("La contraseña actual es incorrecta")
        }
        val usuarioActualizado = usuario.copy(contrasena = newPassword)
        dao.actualizar(usuarioActualizado)
        _usuarioActual.value = usuarioActualizado
    }

    fun observarUsuarios(): Flow<List<Usuario>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Long): Usuario? = dao.obtenerPorId(id)
}
