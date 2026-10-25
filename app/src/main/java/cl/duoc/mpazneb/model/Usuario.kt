package cl.duoc.mpazneb.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["alias"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alias: String,
    val claveHash: String,
    val perfil: Perfil,
    val activo: Boolean = true,
    val ultimoAcceso: Long = System.currentTimeMillis()
)