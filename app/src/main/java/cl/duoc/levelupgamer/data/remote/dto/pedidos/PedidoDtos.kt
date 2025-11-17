package cl.duoc.levelupgamer.data.remote.dto.pedidos

data class PedidoCrearDto(
    val userId: Long,
    val direccionEnvio: String,
    val notas: String? = null
)

data class PedidoRespuestaDto(
    val id: Long,
    val userId: Long,
    val total: Double,
    val estado: String,
    val items: List<PedidoProductoDto>
)

data class PedidoProductoDto(
    val productoId: Long,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
)
