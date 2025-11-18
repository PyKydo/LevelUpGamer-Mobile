package cl.duoc.levelupgamer.model

data class Blog(
    val id: Long,
    val title: String,
    val summary: String,
    val content: String,
    val imageUrl: String? = null,
    val altImage: String? = null,
    val featured: Boolean = false
)
