package cl.duoc.levelupgamer.data.session

data class TokenSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: Long? = null,
    val email: String? = null,
    val role: String? = null
)
