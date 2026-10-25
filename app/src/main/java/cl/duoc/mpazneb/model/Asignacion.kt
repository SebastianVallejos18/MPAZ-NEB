package cl.duoc.mpazneb.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asignaciones",
    foreignKeys = [
        ForeignKey(
            entity = Actividad::class,
            parentColumns = ["id"],
            childColumns = ["actividadId"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["estudianteId"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["asistenteId"]
        )
    ],
    indices = [
        Index("actividadId"),
        Index("estudianteId"),
        Index("asistenteId"),
        Index(value = ["codigoTemporal"], unique = true)
    ]
)
data class Asignacion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actividadId: Long,
    val estudianteId: Long,
    val asistenteId: Long,
    val codigoTemporal: String,
    val fecha: Long = System.currentTimeMillis(),
    val estado: EstadoAsignacion = EstadoAsignacion.Pendiente,
    val intentos: Int = 0,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val observacion: String = ""
)