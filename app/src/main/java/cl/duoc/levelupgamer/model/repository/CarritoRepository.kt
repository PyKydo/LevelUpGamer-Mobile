package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoDto
import cl.duoc.levelupgamer.data.remote.mapper.toEntities
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CarritoRepository(
    private val dao: CarritoItemDao,
    private val productoDao: ProductoDao,
    private val api: LevelUpApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observarCarrito(usuarioId: Long): Flow<List<CarritoItemEntity>> = dao.observarPorUsuario(usuarioId)

    suspend fun sincronizarCarrito(usuarioId: Long) = withContext(ioDispatcher) {
        val remoto = api.getCart(usuarioId)
        persistirCarrito(usuarioId, remoto)
    }

    suspend fun agregar(usuarioId: Long, productoId: Long, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor a cero" }
        withContext(ioDispatcher) {
            val remoto = api.addToCart(usuarioId, productoId, cantidad)
            persistirCarrito(usuarioId, remoto)
        }
    }

    suspend fun actualizarCantidad(usuarioId: Long, itemId: Long, nuevaCantidad: Int) {
        require(nuevaCantidad >= 0) { "La cantidad no puede ser negativa" }
        withContext(ioDispatcher) {
            val item = dao.obtenerPorId(itemId)
                ?: throw IllegalArgumentException("Item no encontrado en el carrito local")
            val productoId = item.productoId
            val remoto = if (nuevaCantidad == 0) {
                api.removeFromCart(usuarioId, productoId)
            } else {
                api.removeFromCart(usuarioId, productoId)
                api.addToCart(usuarioId, productoId, nuevaCantidad)
            }
            persistirCarrito(usuarioId, remoto)
        }
    }

    suspend fun eliminarItem(usuarioId: Long, itemId: Long) {
        withContext(ioDispatcher) {
            val item = dao.obtenerPorId(itemId) ?: return@withContext
            val remoto = api.removeFromCart(usuarioId, item.productoId)
            persistirCarrito(usuarioId, remoto)
        }
    }

    suspend fun limpiarCarrito(usuarioId: Long) {
        withContext(ioDispatcher) {
            api.clearCart(usuarioId)
            dao.eliminarPorUsuario(usuarioId)
        }
    }

    private suspend fun persistirCarrito(usuarioId: Long, remoto: CarritoDto) {
        val productosLocales = cargarProductosLocalmente(remoto)
        val entidades = remoto.toEntities(
            fallbackUserId = usuarioId,
            productoResolver = { productoId -> productosLocales[productoId] }
        )
        dao.reemplazarCarrito(usuarioId, entidades)
        if (usuarioId != 0L) {
            dao.eliminarPorUsuario(0)
        }
    }

    private suspend fun cargarProductosLocalmente(remoto: CarritoDto): Map<Long, Producto> {
        val ids = remoto.items.map { it.productId }.distinct().filter { it != 0L }
        if (ids.isEmpty()) return emptyMap()
        return productoDao.obtenerPorIds(ids).associateBy { it.id }
    }
}
