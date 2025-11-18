package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class CarritoRepositoryTest : StringSpec({

    "agregar inserta un item nuevo cuando no existe" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            coEvery { dao.obtenerPorUsuarioYProducto(1, 2) } returns null
            coEvery { dao.insertar(any<CarritoItemEntity>()) } returns 1

            repository.agregar(1, 2, 3)

            coVerify(exactly = 1) { dao.insertar(any<CarritoItemEntity>()) }
            coVerify(exactly = 0) { dao.actualizar(any()) }
        }
    }

    "agregar incrementa la cantidad cuando el item existe" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            val existente = CarritoItemEntity(id = 5, usuarioId = 1, productoId = 2, cantidad = 4)
            coEvery { dao.obtenerPorUsuarioYProducto(1, 2) } returns existente
            coEvery { dao.actualizar(any()) } just runs

            repository.agregar(1, 2, 2)

            coVerify { dao.actualizar(existente.copy(cantidad = 6)) }
            coVerify(exactly = 0) { dao.insertar(any<CarritoItemEntity>()) }
        }
    }

    "agregar con cantidad invalida lanza excepcion" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)

            shouldThrow<IllegalArgumentException> { repository.agregar(1, 1, 0) }
        }
    }

    "limpiarCarrito elimina todos los items locales" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)

            repository.limpiarCarrito(1)

            coVerify(exactly = 1) { dao.eliminarPorUsuario(1) }
        }
    }

    "actualizarCantidad cambia la cantidad cuando el item pertenece al usuario" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            val item = CarritoItemEntity(id = 10, usuarioId = 1, productoId = 8, cantidad = 2)
            coEvery { dao.obtenerPorId(10) } returns item
            coEvery { dao.actualizar(any()) } just runs

            repository.actualizarCantidad(1, 10, 5)

            coVerify { dao.actualizar(item.copy(cantidad = 5)) }
        }
    }

    "actualizarCantidad elimina cuando la nueva cantidad es cero" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            val item = CarritoItemEntity(id = 10, usuarioId = 1, productoId = 8, cantidad = 2)
            coEvery { dao.obtenerPorId(10) } returns item
            coEvery { dao.eliminar(any()) } just runs

            repository.actualizarCantidad(1, 10, 0)

            coVerify { dao.eliminar(item) }
        }
    }

    "actualizarCantidad ignora items de otros usuarios" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            val item = CarritoItemEntity(id = 10, usuarioId = 99, productoId = 8, cantidad = 2)
            coEvery { dao.obtenerPorId(10) } returns item

            repository.actualizarCantidad(1, 10, 5)

            coVerify(exactly = 0) { dao.actualizar(any()) }
            coVerify(exactly = 0) { dao.eliminar(any()) }
        }
    }

    "eliminarItem borra el item cuando pertenece al usuario" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            val item = CarritoItemEntity(id = 7, usuarioId = 1, productoId = 3, cantidad = 1)
            coEvery { dao.obtenerPorId(7) } returns item
            coEvery { dao.eliminar(item) } just runs

            repository.eliminarItem(1, 7)

            coVerify { dao.eliminar(item) }
        }
    }

    "eliminarItem ignora items de otro usuario" {
        runTest {
            val dao: CarritoItemDao = mockk(relaxed = true)
            val repository = CarritoRepository(dao, Dispatchers.Unconfined)
            val item = CarritoItemEntity(id = 7, usuarioId = 2, productoId = 3, cantidad = 1)
            coEvery { dao.obtenerPorId(7) } returns item

            repository.eliminarItem(1, 7)

            coVerify(exactly = 0) { dao.eliminar(any()) }
        }
    }
})