package cl.duoc.levelupgamer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.Blog
import cl.duoc.levelupgamer.model.repository.BlogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BlogDetailViewModel(private val repo: BlogRepository, private val blogId: Long) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _content = MutableStateFlow<String?>(null)
    val content: StateFlow<String?> = _content.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBlogContent(blog: Blog) {
        Log.d("BlogDetailVM", "loadBlogContent for blogId=${blog.id}")

        if (_content.value != null) {
            Log.d("BlogDetailVM", "Content already loaded, skipping")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val fetched = runCatching { repo.obtenerContenidoBlog(blog.id) }.getOrNull()
                if (!fetched.isNullOrBlank()) {
                    Log.d("BlogDetailVM", "Content fetched via API (length=${fetched.length})")
                    _content.value = fetched
                    return@launch
                }

                val urlSource = blog.content
                if (urlSource.isNotBlank()) {
                    Log.d("BlogDetailVM", "Falling back to direct URL $urlSource")
                    _content.value = fetchMarkdownFromUrl(urlSource)
                } else {
                    Log.d("BlogDetailVM", "No content source available for blogId=${blog.id}")
                    _error.value = "Contenido no disponible"
                }
            } catch (ce: CancellationException) {
                Log.d("BlogDetailVM", "Content load cancelled", ce)
                throw ce
            } catch (t: IOException) {
                Log.e("BlogDetailVM", "Failed to download content", t)
                _error.value = "No fue posible descargar el contenido"
            } catch (t: Exception) {
                Log.e("BlogDetailVM", "Unexpected error loading blog", t)
                _error.value = "Error inesperado al cargar el contenido"
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun fetchMarkdownFromUrl(url: String): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "text/markdown, text/plain, text/html;q=0.8, */*;q=0.1")
            .addHeader("User-Agent", "LevelUpGamer/1.0")
            .build()

        val call = client.newCall(request)

        return@withContext try {
            suspendCancellableCoroutine<String> { cont ->

                cont.invokeOnCancellation {
                    try {
                        call.cancel()
                    } catch (_: Throwable) {
                    }
                }

                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (cont.isCancelled) return
                        cont.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val bodyText = response.body?.string()
                            if (!response.isSuccessful) {
                                val msg = "HTTP ${response.code} ${response.message} when fetching $url; body=${bodyText?.take(500)}"
                                Log.e("BlogDetailVM", msg)
                                cont.resumeWithException(IOException(msg))
                                return
                            }
                            if (bodyText.isNullOrBlank()) {
                                val msg = "Empty body when fetching $url; code=${response.code}"
                                Log.e("BlogDetailVM", msg)
                                cont.resumeWithException(IOException(msg))
                                return
                            }
                            cont.resume(bodyText)
                        } catch (e: Exception) {
                            cont.resumeWithException(e)
                        } finally {
                            response.close()
                        }
                    }
                })
            }
        } catch (e: IOException) {
            throw e
        }
    }

    class Factory(private val repo: BlogRepository, private val blogId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BlogDetailViewModel(repo, blogId) as T
        }
    }
}
