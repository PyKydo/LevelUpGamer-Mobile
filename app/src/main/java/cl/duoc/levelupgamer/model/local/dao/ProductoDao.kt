package cl.duoc.levelupgamer.model.local.dao

import androidx.room.*
import cl.duoc.levelupgamer.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY id DESC")
    fun observarTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Producto?

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(productos: List<Producto>)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()

    @Transaction
    suspend fun reemplazarTodos(productos: List<Producto>) {
        eliminarTodos()
        if (productos.isNotEmpty()) {
            insertar(productos)
        }
    }
}
