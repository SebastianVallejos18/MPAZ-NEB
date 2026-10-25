package cl.duoc.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.mpazneb.model.Asignacion
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignacionDao {
    @Insert
    suspend fun insertar(asignacion: Asignacion): Long

    @Update
    suspend fun actualizar(asignacion: Asignacion)

    @Query("SELECT * FROM asignaciones WHERE codigoTemporal = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): Asignacion?

    @Query("SELECT * FROM asignaciones WHERE id = :id")
    suspend fun buscarPorId(id: Long): Asignacion?

    @Query("SELECT * FROM asignaciones WHERE asistenteId = :asistenteId ORDER BY fecha DESC")
    fun observarDeAsistente(asistenteId: Long): Flow<List<Asignacion>>

    @Query("UPDATE asignaciones SET observacion = :observacion WHERE id = :id")
    suspend fun actualizarObservacion(id: Long, observacion: String)
}