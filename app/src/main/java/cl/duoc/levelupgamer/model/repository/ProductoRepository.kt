package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.mapper.toEntity
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ProductoRepository(
    private val dao: ProductoDao,
    private val securedApi: LevelUpApi,
    private val publicApi: LevelUpApi = securedApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observarProductos(): Flow<List<Producto>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Long): Producto? = dao.obtenerPorId(id)

    suspend fun sincronizarCatalogo() {
        withContext(ioDispatcher) {
            val productosRemotos = fetchRemoteProducts()
            dao.reemplazarTodos(productosRemotos)
        }
    }

    suspend fun limpiarLocal() = withContext(ioDispatcher) { dao.eliminarTodos() }

    private suspend fun fetchRemoteProducts(): List<Producto> {
        val dtos = try {
            securedApi.getProducts()
        } catch (error: Throwable) {
            if (shouldFallbackToPublicApi(error)) {
                publicApi.getProducts()
            } else {
                throw error
            }
        }
        return dtos.map { it.toEntity() }
    }

    private fun shouldFallbackToPublicApi(error: Throwable): Boolean {
        if (publicApi === securedApi) return false
        return (error as? HttpException)?.code() == 401
    }
}
