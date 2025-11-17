package cl.duoc.levelupgamer.data.remote.dto.auth

import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

data class LoginResponse(
    @SerializedName("token") val legacyToken: String? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    val refreshToken: String? = null,
    val rol: String? = null,
    @SerializedName("usuarioId") val usuarioId: Long? = null,
    val usuario: UsuarioRespuestaDto? = null
)

data class TokenRefreshRequest(
    val refreshToken: String
)

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val usuario: UsuarioRespuestaDto? = null,
    val rol: String? = null
)

data class ChangePasswordRequest(
    @SerializedName("contrasenaActual") val currentPassword: String,
    @SerializedName("contrasenaNueva") val newPassword: String
)
