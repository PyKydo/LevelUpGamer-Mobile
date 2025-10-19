package cl.duoc.levelupgamer.viewmodel

class ProductoViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = ProductoRepository(db.productoDao())
        return ProductoViewModel(repo) as T
    }
}