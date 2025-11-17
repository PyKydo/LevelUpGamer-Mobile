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
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(productoDao, api, Dispatchers.Unconfined)

            coEvery { api.getProducts() } returns emptyList<ProductoDto>()

            repository.sincronizarCatalogo()

            coVerify(exactly = 1) { api.getProducts() }

            coVerify(exactly = 1) { productoDao.reemplazarTodos(any()) }
        }
    }

    "al observar productos, debe llamar al dao" {
        runTest {
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(productoDao, api, Dispatchers.Unconfined)

            repository.observarProductos()

            coVerify(exactly = 1) { productoDao.observarTodos() }
        }
    }
})