package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Usuario

interface InAuthRepository {
    val usuarioActual: kotlinx.coroutines.flow.Flow<Usuario?>

    suspend fun registrar(nombre: String, email: String, contrasena: String, fechaNacimiento: String): Usuario
    suspend fun iniciarSesion(email: String, contrasena: String): Usuario
    suspend fun existe(email: String): Boolean
    suspend fun cerrarSesion()
    suspend fun actualizarPerfil(nombre: String, email: String)
}