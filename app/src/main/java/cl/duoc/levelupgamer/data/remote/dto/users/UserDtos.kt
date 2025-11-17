package cl.duoc.levelupgamer.data.remote.dto.users

import com.google.gson.annotations.SerializedName

data class UsuarioRegistroDto(
    @SerializedName("run") val run: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String,
    @SerializedName("region") val region: String,
    @SerializedName("comuna") val comuna: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("codigoReferido") val codigoReferido: String? = null
)

data class UsuarioUpdateDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("region") val region: String? = null,
    @SerializedName("comuna") val comuna: String? = null,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("fotoPerfilUrl") val fotoPerfilUrl: String? = null
)

data class UsuarioRespuestaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("run") val run: String? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellidos") val apellidos: String? = null,
    @SerializedName("correo") val correo: String,
    @SerializedName("rol") val rol: String? = null,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String? = null,
    @SerializedName("fotoPerfilUrl") val fotoPerfilUrl: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("comuna") val comuna: String? = null,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("puntos") val puntos: Int? = null
)

data class RolesResponse(
    val roles: List<String>
)
