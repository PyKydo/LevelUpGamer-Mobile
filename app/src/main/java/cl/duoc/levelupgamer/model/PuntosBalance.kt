package cl.duoc.levelupgamer.model

/**
 * Estado simple del programa de puntos del usuario en el backend.
 */
data class PuntosBalance(
    val userId: Long,
    val puntos: Int,
    val motivo: String? = null
)
