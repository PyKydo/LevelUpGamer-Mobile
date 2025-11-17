package cl.duoc.levelupgamer.data.remote.dto.gamificacion

data class PuntosDto(
    val userId: Long,
    val puntos: Int,
    val motivo: String? = null
)
