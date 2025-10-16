package cl.duoc.levelupgamer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carritos")
data class Carrito(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val items: List<ItemCarrito>
)

