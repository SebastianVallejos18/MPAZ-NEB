package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.local.RespuestaDao
import cl.duoc.mpazneb.model.Respuesta
import kotlinx.coroutines.flow.Flow

class RespuestaRepository(private val respuestaDao: RespuestaDao) {

    suspend fun registrar(respuesta: Respuesta): Long = respuestaDao.insertar(respuesta)

    suspend fun deIntento(asignacionId: Long, intento: Int): List<Respuesta> =
        respuestaDao.deIntento(asignacionId, intento)

    fun observarDeAsignacion(asignacionId: Long): Flow<List<Respuesta>> =
        respuestaDao.observarDeAsignacion(asignacionId)
}