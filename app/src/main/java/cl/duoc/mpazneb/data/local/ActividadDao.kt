package cl.duoc.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Insert
    suspend fun insertar(actividad: Actividad): Long

    @Update
    suspend fun actualizar(actividad: Actividad)

    @Query("SELECT * FROM actividades ORDER BY nombre")
    fun observarTodas(): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun buscarPorId(id: Long): Actividad?

    @Query(
        "SELECT * FROM actividades " +
                "WHERE (:modulo IS NULL OR modulo = :modulo) " +
                "AND (:curso IS NULL OR cursoRecomendado = :curso) " +
                "AND (:dificultad IS NULL OR dificultad = :dificultad) " +
                "AND (:estado IS NULL OR estado = :estado) " +
                "ORDER BY cursoRecomendado, nombre"
    )
    fun filtrar(
        modulo: String?,
        curso: Int?,
        dificultad: String?,
        estado: String?
    ): Flow<List<Actividad>>

    @Query("SELECT COUNT(*) FROM actividades")
    suspend fun contar(): Int
}