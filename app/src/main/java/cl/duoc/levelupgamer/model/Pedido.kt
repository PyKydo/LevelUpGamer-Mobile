package cl.duoc.levelupgamer.model

data class Pedido(
    val id: Long,
    val userId: Long,
    val total: Double,
    val estado: String,
    val items: List<PedidoItem>
)

data class PedidoItem(
    val productoId: Long,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
)
