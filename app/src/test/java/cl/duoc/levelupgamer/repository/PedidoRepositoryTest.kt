package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoCrearDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoRespuestaDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoProductoDto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
            val pedidoDtoMock = PedidoRespuestaDto(
                id = 1L,
                userId = userId,
                total = 150.0,
                estado = "Pendiente",
                items = emptyList()
            )

            coEvery { api.createOrder(any()) } returns pedidoDtoMock

            val result = pedidoRepository.crearPedido(
                userId = userId,
                direccionEnvio = "Calle Falsa 123",
                notas = "Sin notas",
                total = 150.0
            )

            result.id shouldBe 1L
            result.total shouldBe 150.0
            coVerify(exactly = 1) { api.createOrder(any<PedidoCrearDto>()) }
        }
    }

    "al obtener pedidos de usuario, debe llamar a api.getOrdersForUser y mapear la lista de PedidoRespuestaDto" {
        runTest(testDispatcher) {

            val userId = 1L
            val pedidoDto1 = PedidoRespuestaDto(
                id = 1L,
                userId = userId,
                total = 100.0,
                estado = "Entregado",
                items = emptyList()
            )
            val pedidoDto2 = PedidoRespuestaDto(
                id = 2L,
                userId = userId,
                total = 200.0,
                estado = "Enviado",
                items = emptyList()
            )

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
            val pedidoDtoMock = PedidoRespuestaDto(
                id = pedidoId,
                userId = 1L,
                total = 500.0,
                estado = "Cancelado",
                items = emptyList()
            )

            coEvery { api.getOrder(pedidoId) } returns pedidoDtoMock


            val result = pedidoRepository.obtenerPedido(pedidoId)


            result.id shouldBe pedidoId
            result.estado shouldBe "Cancelado"
            coVerify(exactly = 1) { api.getOrder(pedidoId) }
        }
    }

    "crearPedido con items convierte cada CarritoItemEntity en PedidoProductoCrearDto" {
        runTest(testDispatcher) {
            val userId = 3L
            val items = listOf(
                CarritoItemEntity(id = 1, usuarioId = userId, productoId = 10, cantidad = 2),
                CarritoItemEntity(id = 2, usuarioId = userId, productoId = 11, cantidad = 1)
            )
            val payloadSlot = slot<PedidoCrearDto>()
            val response = PedidoRespuestaDto(
                id = 22L,
                userId = userId,
                total = 9990.0,
                estado = "Procesando",
                items = listOf(
                    PedidoProductoDto(productoId = 10, nombre = "Control", cantidad = 2, precioUnitario = 4995.0)
                )
            )

            coEvery { api.createOrder(capture(payloadSlot)) } returns response

            val pedido = pedidoRepository.crearPedido(
                userId = userId,
                items = items,
                direccionEnvio = "Direccion",
                notas = null,
                total = 9990.0
            )

            payloadSlot.captured.usuarioId shouldBe userId
            payloadSlot.captured.items.shouldHaveSize(2)
            payloadSlot.captured.items[0].productoId shouldBe 10
            payloadSlot.captured.items[0].cantidad shouldBe 2
            payloadSlot.captured.items[1].productoId shouldBe 11
            payloadSlot.captured.items[1].cantidad shouldBe 1

            pedido.id shouldBe 22L
            pedido.items.shouldHaveSize(1)
            pedido.items.first().productoId shouldBe 10
        }
    }
})
