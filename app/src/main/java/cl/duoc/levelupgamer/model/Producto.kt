package cl.duoc.levelupgamer.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "productos",)
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imageUrl: String,
    val categoria: String = "",
    val codigo: String = ""
)

