package cl.duoc.levelupgamer.model.local.dao

import androidx.room.*
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoItemDao {
    @Query("SELECT * FROM carrito_items WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<CarritoItemEntity>>

    @Query("SELECT * FROM carrito_items WHERE id = :id")
    suspend fun obtenerPorId(id: Long): CarritoItemEntity?

    @Query("SELECT * FROM carrito_items WHERE usuarioId = :usuarioId AND productoId = :productoId LIMIT 1")
    suspend fun obtenerPorUsuarioYProducto(usuarioId: Long, productoId: Long): CarritoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(entity: CarritoItemEntity): Long

    @Update
    suspend fun actualizar(entity: CarritoItemEntity)

    @Delete
    suspend fun eliminar(entity: CarritoItemEntity)

    @Query("DELETE FROM carrito_items WHERE usuarioId = :usuarioId")
    suspend fun eliminarPorUsuario(usuarioId: Long)
}
