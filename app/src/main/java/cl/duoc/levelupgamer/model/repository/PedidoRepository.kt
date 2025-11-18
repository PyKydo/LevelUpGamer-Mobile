package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoCrearDto
import cl.duoc.levelupgamer.data.remote.mapper.toDomain
import cl.duoc.levelupgamer.model.Pedido
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PedidoRepository(
    private val api: LevelUpApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun crearPedido(userId: Long, items: List<cl.duoc.levelupgamer.model.local.CarritoItemEntity>, direccionEnvio: String, notas: String?): Pedido =
        withContext(ioDispatcher) {
            val itemsDto = items.map { carritoItem ->
                cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoProductoCrearDto(
                    productoId = carritoItem.productoId,
                    cantidad = carritoItem.cantidad
                )
            }
            api.createOrder(
                cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoCrearDto(
                    usuarioId = userId,
                    items = itemsDto,
                    direccionEnvio = direccionEnvio,
                    notas = notas
                )
            ).toDomain()
        }

    suspend fun obtenerPedidos(userId: Long): List<Pedido> = withContext(ioDispatcher) {
        api.getOrdersForUser(userId).map { it.toDomain() }
    }

    suspend fun obtenerPedido(id: Long): Pedido = withContext(ioDispatcher) {
        api.getOrder(id).toDomain()
    }
}
