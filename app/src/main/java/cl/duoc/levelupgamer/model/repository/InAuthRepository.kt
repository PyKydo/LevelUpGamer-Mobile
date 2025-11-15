package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface InAuthRepository {
    val usuarioActual: StateFlow<Usuario?>

    suspend fun registrar(nombre: String, email: String, contrasena: String, fechaNacimiento: String): Usuario
    suspend fun iniciarSesion(email: String, contrasena: String): Usuario
    suspend fun existe(email: String): Boolean
    suspend fun cerrarSesion()
    suspend fun actualizarPerfil(nombre: String, email: String)
    suspend fun changePassword(currentPassword: String, newPassword: String)
}