package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val dao: CarritoItemDao) {

    fun observarCarrito(usuarioId: Long): Flow<List<CarritoItemEntity>> = dao.observarPorUsuario(usuarioId)

    suspend fun agregarOIncrementar(usuarioId: Long, productoId: Long, incremento: Int = 1) {
        require(incremento != 0) { "incremento no puede ser 0" }
        val existente = dao.obtenerPorUsuarioYProducto(usuarioId, productoId)
        if (existente == null) {
            val cantidad = if (incremento > 0) incremento else 0
            if (cantidad > 0) dao.insertar(CarritoItemEntity(usuarioId = usuarioId, productoId = productoId, cantidad = cantidad))
        } else {
            val nuevaCantidad = existente.cantidad + incremento
            if (nuevaCantidad <= 0) dao.eliminar(existente) else dao.actualizar(existente.copy(cantidad = nuevaCantidad))
        }
    }

    suspend fun actualizarCantidad(itemId: Long, nuevaCantidad: Int) {
        val item = dao.obtenerPorId(itemId) ?: return
        if (nuevaCantidad <= 0) dao.eliminar(item) else dao.actualizar(item.copy(cantidad = nuevaCantidad))
    }

    suspend fun eliminar(itemId: Long) {
        dao.obtenerPorId(itemId)?.let { dao.eliminar(it) }
    }

    suspend fun limpiarCarrito(usuarioId: Long) = dao.eliminarPorUsuario(usuarioId)
}
