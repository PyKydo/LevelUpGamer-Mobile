package cl.duoc.levelupgamer.data.remote.dto.contenido

data class BlogDto(
    val id: Long,
    val titulo: String,
    val autor: String? = null,
    val fechaPublicacion: String? = null,
    val descripcionCorta: String? = null,
    val contenidoUrl: String? = null,
    val imagenUrl: String? = null,
    val altImagen: String? = null
)

data class ContactoDto(
    val nombre: String,
    val email: String,
    val mensaje: String
)
