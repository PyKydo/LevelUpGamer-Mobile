package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import cl.duoc.levelupgamer.model.repository.ProductoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull

@ExperimentalCoroutinesApi
class ProductoViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "El ViewModel debe cargar la lista de productos al inicializarse" {
        runTest(testDispatcher) {
            val productoDao: ProductoDao = mockk()
            val api: cl.duoc.levelupgamer.data.remote.api.LevelUpApi = mockk()
            val dummyProducts = listOf(
                Producto(1, "Catan", "Juego de mesa", 29990.0, "", "Juegos de Mesa", "JM001"),
                Producto(2, "PlayStation 5", "Consola de videojuegos", 549990.0, "", "Consolas", "CO001")
            )
            coEvery { productoDao.observarTodos() } returns flowOf(dummyProducts)
            val productoRepository = ProductoRepository(productoDao, api)

            val viewModel = ProductoViewModel(productoRepository)

            val productosState = withTimeoutOrNull(2000) { // Timeout de 2 segundos
                viewModel.productos.first { it.isNotEmpty() }
            }

            productosState shouldContainExactly dummyProducts
        }
    }
})