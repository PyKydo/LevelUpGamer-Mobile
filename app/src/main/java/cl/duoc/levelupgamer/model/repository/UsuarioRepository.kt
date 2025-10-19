package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.local.dao.UsuarioDao
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {

    fun observarUsuarios(): Flow<List<Usuario>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Long): Usuario? = dao.obtenerPorId(id)

    suspend fun obtenerPorEmail(email: String): Usuario? = dao.obtenerPorEmail(email.trim().lowercase())

    suspend fun guardar(usuario: Usuario): Long {
        return if (usuario.id == 0L) {
            dao.insertar(
                usuario.copy(
                    nombre = usuario.nombre.trim(),
                    email = usuario.email.trim().lowercase(),
                    fechaNacimiento = usuario.fechaNacimiento.trim()
                )
            )
        } else {
            dao.actualizar(
                usuario.copy(
                    nombre = usuario.nombre.trim(),
                    email = usuario.email.trim().lowercase(),
                    fechaNacimiento = usuario.fechaNacimiento.trim()
                )
            )
            usuario.id
        }
    }

    suspend fun eliminar(usuario: Usuario) = dao.eliminar(usuario)
    suspend fun eliminarTodos() = dao.eliminarTodos()
}
