package cl.duoc.levelupgamer.data.remote.dto.pedidos

import com.google.gson.annotations.SerializedName

data class PedidoCrearDto(
    val usuarioId: Long,
    val items: List<PedidoProductoCrearDto>,
    val direccionEnvio: String,
    val notas: String? = null
)

data class PedidoProductoCrearDto(
    val productoId: Long,
    val cantidad: Int
)

data class PedidoRespuestaDto(
    val id: Long,
    @SerializedName("usuarioId") val userId: Long,
    val total: Double,
    val estado: String,
    val items: List<PedidoProductoDto>
)

data class PedidoProductoDto(
    val productoId: Long,
    @SerializedName("nombreProducto") val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
)
