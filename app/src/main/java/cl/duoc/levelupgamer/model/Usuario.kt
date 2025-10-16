package cl.duoc.levelupgamer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val fechaNacimiento: String,
    val fotoPerfilUrl: String? = null
)

