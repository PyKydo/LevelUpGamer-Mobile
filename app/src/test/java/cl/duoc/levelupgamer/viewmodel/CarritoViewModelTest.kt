package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@Suppress("unused")
@ExperimentalCoroutinesApi
class CarritoViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()
    val carritoRepository: CarritoRepository = mockk(relaxed = true)
    val pedidoRepository: cl.duoc.levelupgamer.model.repository.PedidoRepository = mockk(relaxed = true)
    val usuarioRepository: cl.duoc.levelupgamer.model.repository.UsuarioRepository = mockk(relaxed = true)
    lateinit var viewModel: CarritoViewModel

    val usuarioIdEjemplo = 1L
    val productoIdEjemplo = 101L

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        viewModel = CarritoViewModel(carritoRepository, pedidoRepository, usuarioRepository, usuarioIdEjemplo)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "al observar items, debe devolver los datos del repositorio" {
        runTest(testDispatcher) {
            val itemsDePrueba = listOf(
                CarritoItemEntity(id = 1, usuarioId = usuarioIdEjemplo, productoId = 101L, cantidad = 2)
            )
            coEvery { carritoRepository.observarCarrito(usuarioIdEjemplo) } returns flowOf(itemsDePrueba)

            viewModel = CarritoViewModel(carritoRepository, pedidoRepository, usuarioRepository, usuarioIdEjemplo)

            val itemsObservados = viewModel.items.drop(1).first()
            itemsObservados shouldBe itemsDePrueba
            coVerify { carritoRepository.observarCarrito(usuarioIdEjemplo) }
        }
    }

    "al llamar a agregar, debe invocar agregarOIncrementar del repositorio" {
        runTest(testDispatcher) {
            val cantidad = 2

            viewModel.agregar(productoIdEjemplo, cantidad)
            
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { carritoRepository.agregar(usuarioIdEjemplo, productoIdEjemplo, cantidad) }
        }
    }

    "al llamar a actualizarCantidad, debe invocar actualizarCantidad del repositorio" {
        runTest(testDispatcher) {
            val itemId = 5L
            val nuevaCantidad = 10

            viewModel.actualizarCantidad(itemId, nuevaCantidad)

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { carritoRepository.actualizarCantidad(usuarioIdEjemplo, itemId, nuevaCantidad) }
        }
    }

    "al llamar a eliminar, debe invocar eliminar del repositorio" {
        runTest(testDispatcher) {
            val itemId = 5L

            viewModel.eliminar(itemId)

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { carritoRepository.eliminarItem(usuarioIdEjemplo, itemId) }
        }
    }

    "al llamar a limpiar, debe invocar limpiarCarrito del repositorio" {
        runTest(testDispatcher) {
            viewModel.limpiar()

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { carritoRepository.limpiarCarrito(usuarioIdEjemplo) }
        }
    }
})
