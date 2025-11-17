package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.mapper.toEntities
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CarritoRepository(
    private val dao: CarritoItemDao,
    private val api: LevelUpApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observarCarrito(usuarioId: Long): Flow<List<CarritoItemEntity>> = dao.observarPorUsuario(usuarioId)

    suspend fun sincronizar(usuarioId: Long) {
        withContext(ioDispatcher) {
            val remoto = api.getCart(usuarioId)
            dao.reemplazarCarrito(usuarioId, remoto.toEntities())
        }
    }

    suspend fun agregar(usuarioId: Long, productoId: Long, cantidad: Int = 1) {
        require(cantidad > 0) { "La cantidad debe ser mayor a cero" }
        withContext(ioDispatcher) {
            val carrito = api.addToCart(usuarioId, productoId, cantidad)
            dao.reemplazarCarrito(usuarioId, carrito.toEntities())
        }
    }

    suspend fun actualizarCantidad(usuarioId: Long, itemId: Long, nuevaCantidad: Int) {
        withContext(ioDispatcher) {
            val item = dao.obtenerPorId(itemId) ?: return@withContext
            if (nuevaCantidad <= 0) {
                removeByProduct(usuarioId, item.productoId)
                return@withContext
            }

            val delta = nuevaCantidad - item.cantidad
            if (delta == 0) {
                return@withContext
            }

            val carritoDto = if (delta > 0) {
                api.addToCart(usuarioId, item.productoId, delta)
            } else {
                api.removeFromCart(usuarioId, item.productoId)
                api.addToCart(usuarioId, item.productoId, nuevaCantidad)
            }
            dao.reemplazarCarrito(usuarioId, carritoDto.toEntities())
        }
    }

    suspend fun eliminarItem(usuarioId: Long, itemId: Long) {
        withContext(ioDispatcher) {
            val item = dao.obtenerPorId(itemId) ?: return@withContext
            removeByProduct(usuarioId, item.productoId)
        }
    }

    suspend fun limpiarCarrito(usuarioId: Long) {
        withContext(ioDispatcher) {
            api.clearCart(usuarioId)
            dao.eliminarPorUsuario(usuarioId)
        }
    }

    private suspend fun removeByProduct(usuarioId: Long, productoId: Long) {
        val carrito = api.removeFromCart(usuarioId, productoId)
        dao.reemplazarCarrito(usuarioId, carrito.toEntities())
    }
}
