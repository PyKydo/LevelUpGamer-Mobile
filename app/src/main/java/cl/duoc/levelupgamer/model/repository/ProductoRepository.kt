package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val dao: ProductoDao) {

    fun observarProductos(): Flow<List<Producto>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Long): Producto? = dao.obtenerPorId(id)

    suspend fun guardar(producto: Producto): Long {
        val normalized = producto.copy(
            nombre = producto.nombre.trim(),
            descripcion = producto.descripcion.trim(),
            imageUrl = producto.imageUrl.trim(),
            categoria = producto.categoria.trim(),
            codigo = producto.codigo.trim()
        )
        return if (producto.id == 0L) dao.insertar(normalized) else {
            dao.actualizar(normalized.copy(id = producto.id)); producto.id
        }
    }

    suspend fun eliminar(producto: Producto) = dao.eliminar(producto)
    suspend fun eliminarTodos() = dao.eliminarTodos()
}
