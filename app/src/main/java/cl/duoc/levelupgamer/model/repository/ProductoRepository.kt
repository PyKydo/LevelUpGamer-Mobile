package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.mapper.toEntity
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductoRepository(
    private val dao: ProductoDao,
    private val api: LevelUpApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observarProductos(): Flow<List<Producto>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Long): Producto? = dao.obtenerPorId(id)

    suspend fun sincronizarCatalogo() {
        withContext(ioDispatcher) {
            val productosRemotos = api.getProducts().map { it.toEntity() }
            dao.reemplazarTodos(productosRemotos)
        }
    }

    suspend fun limpiarLocal() = withContext(ioDispatcher) { dao.eliminarTodos() }
}
