package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.local.RespuestaDao
import cl.duoc.mpazneb.model.Respuesta
import kotlinx.coroutines.flow.Flow

class RespuestaRepository(private val respuestaDao: RespuestaDao) {

    suspend fun insertar(respuesta: Respuesta): Long = respuestaDao.insertar(respuesta)

    fun observarDeAsignacion(asignacionId: Long): Flow<List<Respuesta>> = respuestaDao.observarDeAsignacion(asignacionId)
}