package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CarritoRepository(
    private val dao: CarritoItemDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observarCarrito(usuarioId: Long): Flow<List<CarritoItemEntity>> = dao.observarPorUsuario(usuarioId)

    suspend fun agregar(usuarioId: Long, productoId: Long, cantidad: Int = 1) {
        require(cantidad > 0) { "La cantidad debe ser mayor a cero" }
        withContext(ioDispatcher) {
            val existente = dao.obtenerPorUsuarioYProducto(usuarioId, productoId)
            if (existente != null) {
                dao.actualizar(existente.copy(cantidad = existente.cantidad + cantidad))
            } else {
                dao.insertar(
                    CarritoItemEntity(
                        usuarioId = usuarioId,
                        productoId = productoId,
                        cantidad = cantidad
                    )
                )
            }
        }
    }

    suspend fun actualizarCantidad(usuarioId: Long, itemId: Long, nuevaCantidad: Int) {
        withContext(ioDispatcher) {
            val item = dao.obtenerPorId(itemId) ?: return@withContext
            if (item.usuarioId != usuarioId) return@withContext

            when {
                nuevaCantidad <= 0 -> dao.eliminar(item)
                nuevaCantidad != item.cantidad -> dao.actualizar(item.copy(cantidad = nuevaCantidad))
            }
        }
    }

    suspend fun eliminarItem(usuarioId: Long, itemId: Long) {
        withContext(ioDispatcher) {
            val item = dao.obtenerPorId(itemId) ?: return@withContext
            if (item.usuarioId == usuarioId) {
                dao.eliminar(item)
            }
        }
    }

    suspend fun limpiarCarrito(usuarioId: Long) {
        withContext(ioDispatcher) {
            dao.eliminarPorUsuario(usuarioId)
        }
    }
}
