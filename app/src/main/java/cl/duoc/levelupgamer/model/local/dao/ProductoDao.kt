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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto): Long

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()
}
