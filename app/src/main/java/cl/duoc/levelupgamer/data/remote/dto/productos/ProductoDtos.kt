package cl.duoc.levelupgamer.data.remote.dto.productos

data class ProductoDto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String? = null,
    val codigo: String? = null,
    val imageUrl: String? = null,
    val stock: Int? = null,
    val descuento: Double? = null
)
