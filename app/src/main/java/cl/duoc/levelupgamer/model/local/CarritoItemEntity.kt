package cl.duoc.levelupgamer.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito_items")
data class CarritoItemEntity(
    @PrimaryKey val id: Long,
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int,
    val unitPrice: Double = 0.0
)
