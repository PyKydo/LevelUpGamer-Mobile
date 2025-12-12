package cl.duoc.levelupgamer.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito_items")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int,
    val unitPrice: Double = 0.0,
    val nombre: String = "",
    val descripcion: String = "",
    val codigo: String = "",
    val imageUrl: String = "",
    val categoria: String = "",
    val gallery: List<String> = emptyList()
)
