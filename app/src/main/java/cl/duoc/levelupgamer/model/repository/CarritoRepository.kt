package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val dao: CarritoItemDao) {

    fun observarCarrito(usuarioId: Long): Flow<List<CarritoItemEntity>> = dao.observarPorUsuario(usuarioId)

    suspend fun agregarOIncrementar(usuarioId: Long, productoId: Long, incremento: Int = 1) {
        val existente = dao.obtenerPorUsuarioYProducto(usuarioId, productoId)
        if (existente == null) {
            dao.insertar(CarritoItemEntity(usuarioId = usuarioId, productoId = productoId, cantidad = incremento))
        } else {
            dao.actualizar(existente.copy(cantidad = existente.cantidad + incremento))
        }
    }

    suspend fun actualizarCantidad(itemId: Long, nuevaCantidad: Int) {
        val actual = dao.obtenerPorId(itemId) ?: return
        if (nuevaCantidad <= 0) dao.eliminar(actual) else dao.actualizar(actual.copy(cantidad = nuevaCantidad))
    }

    suspend fun eliminar(itemId: Long) {
        dao.obtenerPorId(itemId)?.let { dao.eliminar(it) }
    }

    suspend fun limpiarCarrito(usuarioId: Long) = dao.eliminarPorUsuario(usuarioId)
}
