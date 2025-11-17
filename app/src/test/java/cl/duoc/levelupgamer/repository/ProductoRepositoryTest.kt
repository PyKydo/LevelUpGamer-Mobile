package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.productos.ProductoDto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import cl.duoc.levelupgamer.model.repository.ProductoRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class ProductoRepositoryTest : StringSpec({

    "al sincronizar catalogo, debe llamar a la api y luego al dao" {
        runTest {
            // 1. Preparación
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(productoDao, api, Dispatchers.Unconfined)

            // Simulamos que la API devuelve una lista vacía de productos DTO
            coEvery { api.getProducts() } returns emptyList<ProductoDto>()

            // 2. Acción
            repository.sincronizarCatalogo()

            // 3. Verificación
            coVerify(exactly = 1) { api.getProducts() }
            // Verificamos que se llamó al DAO para reemplazar los productos locales
            coVerify(exactly = 1) { productoDao.reemplazarTodos(any()) }
        }
    }

    "al observar productos, debe llamar al dao" {
        runTest {
            // 1. Preparación
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(productoDao, api, Dispatchers.Unconfined)

            // 2. Acción
            repository.observarProductos()

            // 3. Verificación
            // Verificamos que se llamó al método del DAO que observa los productos
            coVerify(exactly = 1) { productoDao.observarTodos() }
        }
    }
})