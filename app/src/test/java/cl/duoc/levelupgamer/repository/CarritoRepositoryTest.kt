package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoDto
import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoItemDto
import cl.duoc.levelupgamer.data.remote.dto.productos.ProductoDto
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var carritoDao: FakeCarritoItemDao
    private val productoDao: ProductoDao = mockk(relaxed = true)
    private val api: LevelUpApi = mockk()
    private lateinit var repository: CarritoRepository

    @BeforeEach
    fun setup() {
        carritoDao = FakeCarritoItemDao()
        repository = CarritoRepository(
            dao = carritoDao,
            productoDao = productoDao,
            api = api,
            ioDispatcher = dispatcher
        )
        coEvery { productoDao.obtenerPorIds(any()) } returns emptyList()
    }

    @Test
    fun `sincronizarCarrito persiste lo recibido desde el backend`() = runTest(dispatcher) {
        val remoteDto = carritoDto(quantity = 2, unitPrice = 9990.0, productName = "Control")
        coEvery { api.getCart(USER_ID) } returns remoteDto
        coEvery { productoDao.obtenerPorIds(any()) } returns emptyList()

        repository.sincronizarCarrito(USER_ID)

        val stored = carritoDao.snapshot(USER_ID)
        assertEquals(1, stored.size)
        assertEquals("Control", stored.first().nombre)
        assertEquals(9990.0, stored.first().unitPrice, 0.0)
        coVerify(exactly = 1) { api.getCart(USER_ID) }
        coVerify { productoDao.obtenerPorIds(listOf(PRODUCT_ID)) }
    }

    @Test
    fun `sincronizarCarrito usa el producto local cuando el backend no lo entrega`() = runTest(dispatcher) {
        val remoteDto = carritoDto(unitPrice = null, includeProduct = false)
        val localProduct = productoLocal(nombre = "Nombre local", precio = 1200.0)
        coEvery { api.getCart(USER_ID) } returns remoteDto
        coEvery { productoDao.obtenerPorIds(listOf(PRODUCT_ID)) } returns listOf(localProduct)

        repository.sincronizarCarrito(USER_ID)

        val stored = carritoDao.snapshot(USER_ID)
        val item = stored.first()
        assertEquals("Nombre local", item.nombre)
        assertEquals(1200.0, item.unitPrice, 0.0)
    }

    @Test
    fun `agregar sincroniza el carrito retornado por el backend`() = runTest(dispatcher) {
        val remoteDto = carritoDto(quantity = 3, unitPrice = 4590.0)
        coEvery { api.addToCart(USER_ID, PRODUCT_ID, 3) } returns remoteDto
        coEvery { productoDao.obtenerPorIds(any()) } returns emptyList()

        repository.agregar(USER_ID, PRODUCT_ID, 3)

        val stored = carritoDao.snapshot(USER_ID)
        assertEquals(3, stored.first().cantidad)
        assertEquals(4590.0, stored.first().unitPrice, 0.0)
        coVerify { api.addToCart(USER_ID, PRODUCT_ID, 3) }
    }

    @Test
    fun `eliminarItem limpia el cache cuando la API devuelve carrito vacío`() = runTest(dispatcher) {
        carritoDao.insertar(
            CarritoItemEntity(
                id = 10,
                usuarioId = USER_ID,
                productoId = PRODUCT_ID,
                cantidad = 1,
                unitPrice = 100.0
            )
        )
        coEvery { api.removeFromCart(USER_ID, PRODUCT_ID) } returns CarritoDto(userId = USER_ID, items = emptyList())
        coEvery { productoDao.obtenerPorIds(emptyList()) } returns emptyList()

        repository.eliminarItem(USER_ID, itemId = 10)

        assertEquals(0, carritoDao.snapshot(USER_ID).size)
        coVerify { api.removeFromCart(USER_ID, PRODUCT_ID) }
    }

    @Test
    fun `actualizarCantidad con cero elimina el item sin volver a crearlo`() = runTest(dispatcher) {
        val itemId = carritoDao.insertar(
            CarritoItemEntity(
                id = 11,
                usuarioId = USER_ID,
                productoId = PRODUCT_ID,
                cantidad = 4,
                unitPrice = 100.0
            )
        )
        coEvery { api.removeFromCart(USER_ID, PRODUCT_ID) } returns CarritoDto(userId = USER_ID, items = emptyList())
        coEvery { productoDao.obtenerPorIds(emptyList()) } returns emptyList()

        repository.actualizarCantidad(USER_ID, itemId = itemId, nuevaCantidad = 0)

        assertEquals(0, carritoDao.snapshot(USER_ID).size)
        coVerify(exactly = 1) { api.removeFromCart(USER_ID, PRODUCT_ID) }
        coVerify(exactly = 0) { api.addToCart(any(), any(), any()) }
    }

    @Test
    fun `actualizarCantidad mayor a cero vuelve a sincronizar el carrito remoto`() = runTest(dispatcher) {
        val itemId = carritoDao.insertar(
            CarritoItemEntity(
                id = 12,
                usuarioId = USER_ID,
                productoId = PRODUCT_ID,
                cantidad = 1,
                unitPrice = 100.0
            )
        )
        coEvery { api.removeFromCart(USER_ID, PRODUCT_ID) } returns carritoDto()
        coEvery { api.addToCart(USER_ID, PRODUCT_ID, 7) } returns carritoDto(quantity = 7, unitPrice = 5000.0)
        coEvery { productoDao.obtenerPorIds(listOf(PRODUCT_ID)) } returns emptyList()

        repository.actualizarCantidad(USER_ID, itemId = itemId, nuevaCantidad = 7)

        val stored = carritoDao.snapshot(USER_ID)
        assertEquals(7, stored.first().cantidad)
        assertEquals(5000.0, stored.first().unitPrice, 0.0)
        coVerify(exactly = 1) { api.removeFromCart(USER_ID, PRODUCT_ID) }
        coVerify(exactly = 1) { api.addToCart(USER_ID, PRODUCT_ID, 7) }
    }

    @Test
    fun `sincronizarCarrito con respuesta de invitado reasigna el owner y limpia cache temporal`() = runTest(dispatcher) {
        val invitadoDto = carritoDto(userIdOverride = 0)
        coEvery { api.getCart(USER_ID) } returns invitadoDto
        coEvery { productoDao.obtenerPorIds(any()) } returns emptyList()

        repository.sincronizarCarrito(USER_ID)

        val stored = carritoDao.snapshot(USER_ID)
        assertEquals(USER_ID, stored.first().usuarioId)
        assertEquals(1, carritoDao.eliminacionesPorUsuario(0))
    }

    @Test
    fun `limpiarCarrito borra el cache local luego de notificar al backend`() = runTest(dispatcher) {
        carritoDao.insertar(
            CarritoItemEntity(
                id = 13,
                usuarioId = USER_ID,
                productoId = PRODUCT_ID,
                cantidad = 2,
                unitPrice = 400.0
            )
        )
        coEvery { api.clearCart(USER_ID) } returns Unit

        repository.limpiarCarrito(USER_ID)

        assertEquals(emptyList<CarritoItemEntity>(), carritoDao.snapshot(USER_ID))
        coVerify(exactly = 1) { api.clearCart(USER_ID) }
    }

    @Test
    fun `agregar arroja error cuando la cantidad no es positiva`() = runTest(dispatcher) {
        try {
            repository.agregar(USER_ID, PRODUCT_ID, cantidad = 0)
            fail("Se esperaba IllegalArgumentException")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message.orEmpty().contains("mayor a cero"))
        }
    }

    @Test
    fun `actualizarCantidad arroja error cuando el valor es negativo`() = runTest(dispatcher) {
        try {
            repository.actualizarCantidad(USER_ID, itemId = 99, nuevaCantidad = -1)
            fail("Se esperaba IllegalArgumentException")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message.orEmpty().contains("no puede ser negativa"))
        }
    }

    private fun carritoDto(
        quantity: Int = 1,
        unitPrice: Double? = 1990.0,
        productName: String = "Producto remoto",
        includeProduct: Boolean = true,
        userIdOverride: Long? = null
    ): CarritoDto {
        val productDto = if (includeProduct) {
            ProductoDto(
                id = PRODUCT_ID,
                nombre = productName,
                descripcion = "Descripcion",
                precio = unitPrice?.let { BigDecimal.valueOf(it) },
                codigo = "SKU-1"
            )
        } else {
            null
        }
        return CarritoDto(
            userId = userIdOverride ?: USER_ID,
            items = listOf(
                CarritoItemDto(
                    id = 1L,
                    productId = PRODUCT_ID,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    product = productDto
                )
            )
        )
    }

    private fun productoLocal(nombre: String, precio: Double) = Producto(
        id = PRODUCT_ID,
        nombre = nombre,
        descripcion = nombre,
        precio = precio,
        imageUrl = "",
        categoria = "",
        codigo = "SKU-LOCAL",
        stock = 0,
        descuento = null,
        gallery = emptyList()
    )

    private companion object {
        const val USER_ID = 1L
        const val PRODUCT_ID = 99L
    }
}

private class FakeCarritoItemDao : CarritoItemDao {
    private val items = mutableListOf<CarritoItemEntity>()
    private val flows = mutableMapOf<Long, MutableStateFlow<List<CarritoItemEntity>>>()
    private val eliminaciones = mutableListOf<Long>()

    override fun observarPorUsuario(usuarioId: Long): Flow<List<CarritoItemEntity>> =
        flows.getOrPut(usuarioId) { MutableStateFlow(items.filter { it.usuarioId == usuarioId }) }

    override suspend fun obtenerPorId(id: Long): CarritoItemEntity? = items.find { it.id == id }

    override suspend fun obtenerPorUsuarioYProducto(usuarioId: Long, productoId: Long): CarritoItemEntity? =
        items.find { it.usuarioId == usuarioId && it.productoId == productoId }

    suspend fun snapshot(usuarioId: Long): List<CarritoItemEntity> =
        items.filter { it.usuarioId == usuarioId }

    override suspend fun insertar(item: CarritoItemEntity): Long {
        val assignedId = if (item.id == 0L) (items.maxOfOrNull { it.id } ?: 0L) + 1 else item.id
        items.removeAll { it.id == assignedId }
        items.add(item.copy(id = assignedId))
        notifyUser(item.usuarioId)
        return assignedId
    }

    override suspend fun insertar(items: List<CarritoItemEntity>) {
        items.forEach { insertar(it) }
    }

    override suspend fun actualizar(item: CarritoItemEntity) {
        items.replaceAll { existing -> if (existing.id == item.id) item else existing }
        notifyUser(item.usuarioId)
    }

    override suspend fun eliminar(item: CarritoItemEntity) {
        items.removeAll { it.id == item.id }
        notifyUser(item.usuarioId)
    }

    override suspend fun eliminarPorUsuario(usuarioId: Long) {
        eliminaciones += usuarioId
        items.removeAll { it.usuarioId == usuarioId }
        notifyUser(usuarioId)
    }

    override suspend fun eliminarPorUsuarioYProducto(usuarioId: Long, productoId: Long) {
        eliminaciones += usuarioId
        items.removeAll { it.usuarioId == usuarioId && it.productoId == productoId }
        notifyUser(usuarioId)
    }

    override suspend fun reemplazarCarrito(usuarioId: Long, nuevos: List<CarritoItemEntity>) {
        eliminarPorUsuario(usuarioId)
        insertar(nuevos)
        notifyUser(usuarioId)
    }

    fun eliminacionesPorUsuario(usuarioId: Long): Int = eliminaciones.count { it == usuarioId }

    private fun notifyUser(usuarioId: Long) {
        val snapshot = items.filter { it.usuarioId == usuarioId }
        flows.getOrPut(usuarioId) { MutableStateFlow(snapshot) }.value = snapshot
    }
}
