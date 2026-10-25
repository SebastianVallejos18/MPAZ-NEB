package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.local.ActividadDao
import cl.duoc.mpazneb.data.local.PreguntaDao
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Pregunta
import kotlinx.coroutines.flow.Flow

class ActividadRepository(
    private val actividadDao: ActividadDao,
    private val preguntaDao: PreguntaDao
) {

    fun observarTodas(): Flow<List<Actividad>> = actividadDao.observarTodas()

    fun filtrar(
        modulo: Modulo,
        curso: Int,
        dificultad: Dificultad,
        estado: EstadoActividad
    ): Flow<List<Actividad>> = actividadDao.filtrar(modulo, curso, dificultad, estado)

    suspend fun buscarPorId(id: Long): Actividad? = actividadDao.buscarPorId(id)

    suspend fun preguntasDe(actividadId: Long): List<Pregunta> =
        preguntaDao.deActividad(actividadId)

    suspend fun actualizar(actividad: Actividad) = actividadDao.actualizar(actividad)

    suspend fun crear(actividad: Actividad, preguntas: List<Pregunta>): Long {
        val idNuevo = actividadDao.insertar(actividad)
        preguntaDao.insertarVarias(preguntas.map { it.copy(actividadId = idNuevo) })
        return idNuevo
    }

    suspend fun contar(): Int = actividadDao.contar()
}