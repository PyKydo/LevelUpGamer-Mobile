package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.model.Blog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.ResponseBody
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class BlogRepository(
    private val api: LevelUpApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun obtenerBlogs(): List<Blog> = withContext(ioDispatcher) {
        val dtos = api.getBlogPosts()
        dtos.map { dto ->


            val summary = dto.descripcionCorta ?: ""
            val content = dto.contenidoUrl ?: ""
            val image = dto.imagenUrl

            Blog(
                id = dto.id,
                title = dto.titulo,
                summary = summary,
                content = content,
                imageUrl = image,
                altImage = dto.altImagen,
                featured = false
            )
        }
    }

    suspend fun obtenerBlogPorId(id: Long): Blog? = withContext(ioDispatcher) {
        return@withContext try {
            val dto = api.getBlogPost(id)
            val summary = dto.descripcionCorta ?: ""

            val contentField = try {

                val contenidoProp = dto.javaClass.getDeclaredField("contenido")
                contenidoProp.isAccessible = true
                contenidoProp.get(dto) as? String
            } catch (_: Throwable) {
                null
            }
            val content = contenidoPropOrNull(contentField = contentField, contenidoUrl = dto.contenidoUrl)
            Blog(
                id = dto.id,
                title = dto.titulo,
                summary = summary,
                content = content ?: "",
                imageUrl = dto.imagenUrl,
                altImage = dto.altImagen,
                featured = false
            )
        } catch (t: Exception) {
            null
        }
    }

    private fun contenidoPropOrNull(contentField: String?, contenidoUrl: String?): String? {
        return when {
            !contentField.isNullOrBlank() -> contentField
            !contenidoUrl.isNullOrBlank() -> contenidoUrl
            else -> null
        }
    }

    suspend fun obtenerContenidoBlog(id: Long): String? = withContext(ioDispatcher) {
        return@withContext runCatching {
            api.getBlogContent(id).use { body ->
                decodeBody(body).takeIf { it.isNotBlank() }
            }
        }.getOrNull()
    }

    private fun decodeBody(body: ResponseBody): String {
        val mediaType: MediaType? = body.contentType()
        val charset: Charset = try {
            mediaType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        } catch (_: Exception) {
            StandardCharsets.UTF_8
        }
        val bytes = body.bytes()
        return String(bytes, charset)
    }
}
