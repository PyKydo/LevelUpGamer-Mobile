package cl.duoc.levelupgamer.data.remote.dto.contenido

data class BlogDto(
    val id: Long,
    val titulo: String,
    val contenido: String,
    val autor: String? = null,
    val publicadoEn: String? = null
)

data class ContactoDto(
    val nombre: String,
    val email: String,
    val mensaje: String
)
