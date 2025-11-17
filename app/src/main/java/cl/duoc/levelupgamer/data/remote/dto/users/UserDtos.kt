package cl.duoc.levelupgamer.data.remote.dto.users

data class UsuarioRegistroDto(
    val nombre: String,
    val run: String? = null,
    val apellido: String? = null,
    val email: String,
    val password: String,
    val fechaNacimiento: String,
    val telefono: String? = null,
    val region: String? = null,
    val comuna: String? = null,
    val direccion: String? = null
)

data class UsuarioUpdateDto(
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val fechaNacimiento: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val fotoPerfilUrl: String? = null
)

data class UsuarioRespuestaDto(
    val id: Long,
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val rol: String? = null,
    val fechaNacimiento: String? = null,
    val fotoPerfilUrl: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val puntos: Int? = null
)

data class RolesResponse(
    val roles: List<String>
)
