package cl.duoc.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.mpazneb.model.Pregunta
import kotlinx.coroutines.flow.Flow

@Dao
interface PreguntaDao {
    @Insert
    suspend fun insertar(pregunta: Pregunta): Long

    @Insert
    suspend fun insertarVarias(preguntas: List<Pregunta>)

    @Update
    suspend fun actualizar(pregunta: Pregunta)

    @Delete
    suspend fun eliminar(pregunta: Pregunta)

    @Query("SELECT * FROM preguntas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Pregunta?

    @Query("SELECT * FROM preguntas WHERE actividadId = :actividadId ORDER BY id")
    suspend fun deActividad(actividadId: Long): List<Pregunta>

    @Query("SELECT * FROM preguntas WHERE actividadId = :actividadId ORDER BY id")
    fun observarDeActividad(actividadId: Long): Flow<List<Pregunta>>

    @Query("SELECT COUNT(*) FROM preguntas WHERE actividadId = :actividadId")
    suspend fun contarDeActividad(actividadId: Long): Int
}