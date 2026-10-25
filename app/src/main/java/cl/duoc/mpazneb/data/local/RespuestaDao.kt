package cl.duoc.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.mpazneb.model.Respuesta
import kotlinx.coroutines.flow.Flow

@Dao
interface RespuestaDao {
    @Insert
    suspend fun insertar(respuesta: Respuesta): Long

    @Query("SELECT * FROM respuestas WHERE asignacionId = :asignacionId ORDER BY id")
    fun observarDeAsignacion(asignacionId: Long): Flow<List<Respuesta>>

    @Query("SELECT * FROM respuestas WHERE asignacionId = :asignacionId AND intento = :intento ORDER BY id")
    suspend fun deIntento(asignacionId: Long, intento: Int): List<Respuesta>
}