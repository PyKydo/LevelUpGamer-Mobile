package cl.duoc.levelupgamer.data.remote.dto.pedidos

import com.google.gson.annotations.SerializedName

data class PedidoCrearDto(
    @SerializedName("cliente") val usuarioId: Long,
    @SerializedName(value = "detalles", alternate = ["items"]) val items: List<PedidoProductoCrearDto>,
    val direccionEnvio: String,
    val notas: String? = null,
    @SerializedName("total") val total: Double
)

data class PedidoProductoCrearDto(
    val productoId: Long,
    val cantidad: Int
)

data class PedidoRespuestaDto(
    val id: Long,
    @SerializedName(value = "usuarioId", alternate = ["cliente"]) val userId: Long,
    val total: Double,
    @SerializedName("totalAntesDescuentos") val totalAntesDescuentos: Double? = null,
    @SerializedName("descuentoCupon") val descuentoCupon: Double? = null,
    @SerializedName("descuentoDuoc") val descuentoDuoc: Double? = null,
    val estado: String,
    val fecha: String? = null,
    @SerializedName(value = "detalles", alternate = ["items"]) val items: List<PedidoProductoDto>
)

data class PedidoProductoDto(
    val productoId: Long,
    @SerializedName(value = "nombre", alternate = ["nombreProducto"]) val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double? = null
)
