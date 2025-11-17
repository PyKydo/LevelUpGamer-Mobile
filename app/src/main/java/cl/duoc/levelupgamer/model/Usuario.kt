package cl.duoc.levelupgamer.model

data class Usuario(
    val id: Long,
    val run: String? = null,
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val rol: String? = null,
    val fechaNacimiento: String = "",
    val fotoPerfilUrl: String? = null,
    val region: String? = null,
    val comuna: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val puntos: Int = 0
)