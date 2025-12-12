package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoRepositoryTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var carritoDao: FakeCarritoItemDao
    private lateinit var repository: CarritoRepository

    @Before
    fun setup() {
        carritoDao = FakeCarritoItemDao()
        repository = CarritoRepository(
            dao = carritoDao,
            ioDispatcher = dispatcher
        )
    }

    @Test
    fun `agregar crea nuevo item cuando no existe`() = runTest(dispatcher) {
        repository.agregar(USER_ID, productoId = 42, cantidad = 2)

        val stored = carritoDao.snapshot(USER_ID)
        assertEquals(1, stored.size)
        assertEquals(42, stored.first().productoId)
        assertEquals(2, stored.first().cantidad)
    }

    @Test
    fun `agregar acumula cantidad cuando item existe`() = runTest(dispatcher) {
        repository.agregar(USER_ID, productoId = 10, cantidad = 1)
        repository.agregar(USER_ID, productoId = 10, cantidad = 3)

        val stored = carritoDao.snapshot(USER_ID)
        assertEquals(1, stored.size)
        assertEquals(4, stored.first().cantidad)
    }

    @Test
    fun `actualizarCantidad aplica nueva cantidad o elimina al llegar a cero`() = runTest(dispatcher) {
        repository.agregar(USER_ID, productoId = 5, cantidad = 2)
        val item = carritoDao.snapshot(USER_ID).first()

        repository.actualizarCantidad(USER_ID, item.id, nuevaCantidad = 5)
        assertEquals(5, carritoDao.snapshot(USER_ID).first().cantidad)

        repository.actualizarCantidad(USER_ID, item.id, nuevaCantidad = 0)
        assertEquals(0, carritoDao.snapshot(USER_ID).size)
    }

    @Test
    fun `eliminarItem borra registro cuando pertenece al usuario`() = runTest(dispatcher) {
        repository.agregar(USER_ID, productoId = 7, cantidad = 1)
        val item = carritoDao.snapshot(USER_ID).first()

        repository.eliminarItem(USER_ID, item.id)

        assertEquals(0, carritoDao.snapshot(USER_ID).size)
    }

    @Test
    fun `limpiarCarrito elimina todos los items del usuario`() = runTest(dispatcher) {
        repository.agregar(USER_ID, productoId = 1, cantidad = 1)
        repository.agregar(USER_ID, productoId = 2, cantidad = 1)

        repository.limpiarCarrito(USER_ID)

        assertEquals(0, carritoDao.snapshot(USER_ID).size)
    }

    private companion object {
        const val USER_ID = 1L
    }
}

private class FakeCarritoItemDao : CarritoItemDao {
    private val items = mutableListOf<CarritoItemEntity>()
    private val flows = mutableMapOf<Long, MutableStateFlow<List<CarritoItemEntity>>>()

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
        items.removeAll { it.usuarioId == usuarioId }
        notifyUser(usuarioId)
    }

    override suspend fun eliminarPorUsuarioYProducto(usuarioId: Long, productoId: Long) {
        items.removeAll { it.usuarioId == usuarioId && it.productoId == productoId }
        notifyUser(usuarioId)
    }

    override suspend fun reemplazarCarrito(usuarioId: Long, nuevos: List<CarritoItemEntity>) {
        eliminarPorUsuario(usuarioId)
        insertar(nuevos)
        notifyUser(usuarioId)
    }

    private fun notifyUser(usuarioId: Long) {
        val snapshot = items.filter { it.usuarioId == usuarioId }
        flows.getOrPut(usuarioId) { MutableStateFlow(snapshot) }.value = snapshot
    }
}
