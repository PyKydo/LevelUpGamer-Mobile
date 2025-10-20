package cl.duoc.levelupgamer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resenas")
data class Resena(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val productoId: Long,
    val calificacion: Int,
    val comentario: String? = null,
    val fecha: Long = System.currentTimeMillis()
)

