package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.productos.ProductoDto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import cl.duoc.levelupgamer.model.repository.ProductoRepository
import cl.duoc.levelupgamer.model.Producto
import io.kotest.core.spec.style.StringSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class ProductoRepositoryTest : StringSpec({

    "al sincronizar catalogo, debe llamar a la api y luego al dao" {
        runTest {
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(
                dao = productoDao,
                securedApi = api,
                publicApi = api,
                ioDispatcher = Dispatchers.Unconfined
            )

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
            val repository = ProductoRepository(
                dao = productoDao,
                securedApi = api,
                publicApi = api,
                ioDispatcher = Dispatchers.Unconfined
            )

            repository.observarProductos()

            coVerify(exactly = 1) { productoDao.observarTodos() }
        }
    }

    "debe usar la api publica cuando la segura responde 401" {
        runTest {
            val productoDao: ProductoDao = mockk(relaxed = true)
            val securedApi: LevelUpApi = mockk(relaxed = true)
            val publicApi: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(
                dao = productoDao,
                securedApi = securedApi,
                publicApi = publicApi,
                ioDispatcher = Dispatchers.Unconfined
            )

            val unauthorizedResponse: Response<List<ProductoDto>> = Response.error(
                401,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )

            coEvery { securedApi.getProducts() } throws HttpException(unauthorizedResponse)
            coEvery { publicApi.getProducts() } returns emptyList<ProductoDto>()

            repository.sincronizarCatalogo()

            coVerify(exactly = 1) { securedApi.getProducts() }
            coVerify(exactly = 1) { publicApi.getProducts() }
            coVerify(exactly = 1) { productoDao.reemplazarTodos(any()) }
        }
    }

    "obtenerPorId delega directamente al dao" {
        runTest {
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(
                dao = productoDao,
                securedApi = api,
                publicApi = api,
                ioDispatcher = Dispatchers.Unconfined
            )

            val esperado = Producto(id = 55, nombre = "Mouse", descripcion = "Gamer", precio = 19990.0, imageUrl = "")
            coEvery { productoDao.obtenerPorId(55) } returns esperado

            val resultado = repository.obtenerPorId(55)

            resultado shouldBe esperado
            coVerify(exactly = 1) { productoDao.obtenerPorId(55) }
        }
    }

    "limpiarLocal borra el catálogo completo desde el dao" {
        runTest {
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(
                dao = productoDao,
                securedApi = api,
                publicApi = api,
                ioDispatcher = Dispatchers.Unconfined
            )

            repository.limpiarLocal()

            coVerify(exactly = 1) { productoDao.eliminarTodos() }
        }
    }

    "sincronizarCatalogo relanza el error si no es posible usar la api pública" {
        runTest {
            val productoDao: ProductoDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = ProductoRepository(
                dao = productoDao,
                securedApi = api,
                publicApi = api,
                ioDispatcher = Dispatchers.Unconfined
            )

            val unauthorizedResponse: Response<List<ProductoDto>> = Response.error(
                401,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )

            coEvery { api.getProducts() } throws HttpException(unauthorizedResponse)

            shouldThrow<HttpException> {
                repository.sincronizarCatalogo()
            }

            coVerify(exactly = 1) { api.getProducts() }
            coVerify(exactly = 0) { productoDao.reemplazarTodos(any()) }
        }
    }
})