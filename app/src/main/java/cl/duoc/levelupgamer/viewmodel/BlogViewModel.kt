package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import cl.duoc.levelupgamer.model.Blog
import cl.duoc.levelupgamer.model.repository.BlogRepository

class BlogViewModel(private val repo: BlogRepository) : ViewModel() {
    private val _blogs = MutableStateFlow<List<Blog>>(emptyList())
    val blogs: StateFlow<List<Blog>> = _blogs.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBlogs()
    }

    private fun loadBlogs() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            runCatching { repo.obtenerBlogs() }
                .onSuccess { _blogs.value = it }
                .onFailure { throwable ->
                    _error.value = throwable.message ?: "Error al cargar blogs"
                }
            _loading.value = false
        }
    }

    class Factory(private val repo: BlogRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BlogViewModel(repo) as T
        }
    }
}
