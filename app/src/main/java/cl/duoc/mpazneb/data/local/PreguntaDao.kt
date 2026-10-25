package cl.duoc.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.mpazneb.model.Pregunta

@Dao
interface PreguntaDao {
    @Insert
    suspend fun insertar(pregunta: Pregunta): Long

    @Insert
    suspend fun insertarVarias(preguntas: List<Pregunta>)

    @Query("SELECT * FROM preguntas WHERE actividadId = :actividadId ORDER BY id")
    suspend fun deActividad(actividadId: Long): List<Pregunta>

    @Query("SELECT COUNT(*) FROM preguntas WHERE actividadId = :actividadId")
    suspend fun contarDeActividad(actividadId: Long): Int
}