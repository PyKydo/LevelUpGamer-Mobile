package cl.duoc.levelupgamer.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val rol: String? = null,
    val fechaNacimiento: String = "",
    val fotoPerfilUrl: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val puntos: Int = 0
)