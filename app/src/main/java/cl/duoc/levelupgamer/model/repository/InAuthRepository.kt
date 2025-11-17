package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface InAuthRepository {
    val usuarioActual: StateFlow<Usuario?>

    suspend fun registrar(
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
    ): Usuario
    suspend fun iniciarSesion(email: String, contrasena: String): Usuario
    suspend fun cerrarSesion()
    suspend fun actualizarPerfil(nombre: String, email: String)
    suspend fun changePassword(currentPassword: String, newPassword: String)
}