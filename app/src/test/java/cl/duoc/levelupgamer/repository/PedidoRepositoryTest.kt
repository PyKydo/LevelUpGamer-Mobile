package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoCrearDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoRespuestaDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@Suppress("unused")
@ExperimentalCoroutinesApi
class PedidoRepositoryTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()
    val api: LevelUpApi = mockk()
    lateinit var pedidoRepository: PedidoRepository

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        pedidoRepository = PedidoRepository(api, testDispatcher)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "al crear pedido, debe llamar a api.createOrder y mapear el PedidoRespuestaDto devuelto" {
        runTest(testDispatcher) {

            val userId = 1L
            val pedidoDtoMock = mockk<PedidoRespuestaDto>()


            coEvery { pedidoDtoMock.id } returns 1L
            coEvery { pedidoDtoMock.userId } returns userId
            coEvery { pedidoDtoMock.total } returns 150.0
            coEvery { pedidoDtoMock.estado } returns "Pendiente"
            coEvery { pedidoDtoMock.items } returns emptyList()

            coEvery { api.createOrder(any()) } returns pedidoDtoMock


            val result = pedidoRepository.crearPedido(userId, "Calle Falsa 123", "Sin notas")


            result.id shouldBe 1L
            result.total shouldBe 150.0
            coVerify(exactly = 1) { api.createOrder(any<PedidoCrearDto>()) }
        }
    }

    "al obtener pedidos de usuario, debe llamar a api.getOrdersForUser y mapear la lista de PedidoRespuestaDto" {
        runTest(testDispatcher) {

            val userId = 1L
            val pedidoDto1 = mockk<PedidoRespuestaDto>()
            val pedidoDto2 = mockk<PedidoRespuestaDto>()
            
            coEvery { pedidoDto1.id } returns 1L
            coEvery { pedidoDto1.userId } returns userId
            coEvery { pedidoDto1.total } returns 100.0
            coEvery { pedidoDto1.estado } returns "Entregado"
            coEvery { pedidoDto1.items } returns emptyList()

            coEvery { pedidoDto2.id } returns 2L
            coEvery { pedidoDto2.userId } returns userId
            coEvery { pedidoDto2.total } returns 200.0
            coEvery { pedidoDto2.estado } returns "Enviado"
            coEvery { pedidoDto2.items } returns emptyList()

            coEvery { api.getOrdersForUser(userId) } returns listOf(pedidoDto1, pedidoDto2)


            val result = pedidoRepository.obtenerPedidos(userId)
            result.size shouldBe 2
            result[0].id shouldBe 1L
            result[1].id shouldBe 2L
            coVerify(exactly = 1) { api.getOrdersForUser(userId) }
        }
    }

    "al obtener un pedido por id, debe llamar a api.getOrder y mapear el resultado" {
        runTest(testDispatcher) {

            val pedidoId = 5L
            val pedidoDtoMock = mockk<PedidoRespuestaDto>()

            coEvery { pedidoDtoMock.id } returns pedidoId
            coEvery { pedidoDtoMock.userId } returns 1L
            coEvery { pedidoDtoMock.total } returns 500.0
            coEvery { pedidoDtoMock.estado } returns "Cancelado"
            coEvery { pedidoDtoMock.items } returns emptyList()

            coEvery { api.getOrder(pedidoId) } returns pedidoDtoMock


            val result = pedidoRepository.obtenerPedido(pedidoId)


            result.id shouldBe pedidoId
            result.estado shouldBe "Cancelado"
            coVerify(exactly = 1) { api.getOrder(pedidoId) }
        }
    }
})
