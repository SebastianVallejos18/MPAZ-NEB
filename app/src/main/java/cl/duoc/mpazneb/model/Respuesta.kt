package cl.duoc.mpazneb.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "respuestas",
    foreignKeys = [
        ForeignKey(
            entity = Asignacion::class,
            parentColumns = ["id"],
            childColumns = ["asignacionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Pregunta::class,
            parentColumns = ["id"],
            childColumns = ["preguntaId"]
        )
    ],
    indices = [Index("asignacionId"), Index("preguntaId")]
)
data class Respuesta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val asignacionId: Long,
    val preguntaId: Long,
    val intento: Int,
    val opcionElegida: Int,
    val esCorrecta: Boolean,
    val fecha: Long = System.currentTimeMillis()
)