package cl.duoc.levelupgamer.data.remote.dto.carrito

data class CarritoDto(
    val userId: Long,
    val items: List<CarritoItemDto> = emptyList(),
    val total: Double? = null
)

data class CarritoItemDto(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double? = null
)
