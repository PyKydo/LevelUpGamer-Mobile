package cl.duoc.levelupgamer.model

data class PuntosBalance(
    val userId: Long,
    val puntos: Int,
    val motivo: String? = null
)
