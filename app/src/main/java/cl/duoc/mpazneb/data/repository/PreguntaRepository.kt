package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.local.PreguntaDao
import cl.duoc.mpazneb.model.Pregunta

class PreguntaRepository(private val preguntaDao: PreguntaDao) {

    suspend fun insertar(pregunta: Pregunta): Long = preguntaDao.insertar(pregunta)

    suspend fun insertarVarias(preguntas: List<Pregunta>) = preguntaDao.insertarVarias(preguntas)

    suspend fun deActividad(actividadId: Long): List<Pregunta> = preguntaDao.deActividad(actividadId)

    suspend fun contarDeActividad(actividadId: Long): Int = preguntaDao.contarDeActividad(actividadId)
}