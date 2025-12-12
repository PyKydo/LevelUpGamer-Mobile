package cl.duoc.levelupgamer.data.remote.dto.productos

import java.math.BigDecimal

data class ProductoDto(
    val id: Long,
    val codigo: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val precio: BigDecimal? = null,
    val stock: Int? = null,
    val stockCritico: Int? = null,
    val categoria: CategoriaDto? = null,
    val puntosLevelUp: Int? = null,
    val imagenes: List<String>? = null,
    val activo: Boolean? = null,
    val vendedor: VendedorResumenDto? = null,
    val imageUrl: String? = null,
    val descuento: Double? = null
)

data class CategoriaDto(
    val id: Long? = null,
    val codigo: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val activo: Boolean? = null
)

data class VendedorResumenDto(
    val id: Long? = null,
    val nombre: String? = null,
    val correo: String? = null,
    val corporativo: Boolean? = null
)
