package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.local.AsignacionDao
import cl.duoc.mpazneb.model.Asignacion
import kotlinx.coroutines.flow.Flow

class AsignacionRepository(private val asignacionDao: AsignacionDao) {

    suspend fun insertar(asignacion: Asignacion): Long = asignacionDao.insertar(asignacion)

    suspend fun actualizar(asignacion: Asignacion) = asignacionDao.actualizar(asignacion)

    suspend fun buscarPorCodigo(codigo: String): Asignacion? = asignacionDao.buscarPorCodigo(codigo)

    suspend fun buscarPorId(id: Long): Asignacion? = asignacionDao.buscarPorId(id)

    fun observarDeAsistente(asistenteId: Long): Flow<List<Asignacion>>
    = asignacionDao.observarDeAsistente(asistenteId)
}