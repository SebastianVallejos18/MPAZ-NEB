package cl.duoc.mpazneb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.model.Respuesta
import cl.duoc.mpazneb.model.Usuario

@Database(
    entities = [Usuario::class, Actividad::class, Pregunta::class, Asignacion::class, Respuesta::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val usuarioDao: UsuarioDao
    abstract val actividadDao: ActividadDao
    abstract val preguntaDao: PreguntaDao
    abstract val asignacionDao: AsignacionDao
    abstract val respuestaDao: RespuestaDao
}