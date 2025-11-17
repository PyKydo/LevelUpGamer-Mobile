package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoDto
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class CarritoRepositoryTest : StringSpec({

    "al agregar un item, debe llamar a la api y al dao" {
        runTest {
            // 1. Preparación
            val carritoDao: CarritoItemDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = CarritoRepository(carritoDao, api, Dispatchers.Unconfined)

            val usuarioId = 1L
            val productoId = 101L
            val cantidad = 1
            val mockCarritoDto: CarritoDto = mockk()
            every { mockCarritoDto.items } returns emptyList()

            coEvery { api.addToCart(usuarioId, productoId, cantidad) } returns mockCarritoDto

            // 2. Acción
            repository.agregar(usuarioId, productoId, cantidad)

            // 3. Verificación
            coVerify(exactly = 1) { api.addToCart(usuarioId, productoId, cantidad) }
            coVerify(exactly = 1) { carritoDao.reemplazarCarrito(usuarioId, any()) }
        }
    }

    "al limpiar el carrito, debe llamar a la api y al dao" {
        runTest {
            // 1. Preparación
            val carritoDao: CarritoItemDao = mockk(relaxed = true)
            val api: LevelUpApi = mockk(relaxed = true)
            val repository = CarritoRepository(carritoDao, api, Dispatchers.Unconfined)
            val usuarioId = 1L

            // La función clearCart devuelve Unit, por lo que se mockea de esta forma.
            coEvery { api.clearCart(usuarioId) } returns Unit

            // 2. Acción
            repository.limpiarCarrito(usuarioId)

            // 3. Verificación
            coVerify(exactly = 1) { api.clearCart(usuarioId) }
            coVerify(exactly = 1) { carritoDao.eliminarPorUsuario(usuarioId) }
        }
    }
})