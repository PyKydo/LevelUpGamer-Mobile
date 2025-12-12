package cl.duoc.levelupgamer.data.remote.dto.gamificacion

import com.google.gson.annotations.SerializedName

data class PuntosDto(
    @SerializedName(value = "usuarioId", alternate = ["userId"]) val userId: Long,
    @SerializedName(value = "puntosAcumulados", alternate = ["puntos"]) val puntos: Int,
    val motivo: String? = null
)
