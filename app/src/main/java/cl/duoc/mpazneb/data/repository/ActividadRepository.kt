package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.remote.aDto
import cl.duoc.mpazneb.data.remote.aModelo
import cl.duoc.mpazneb.data.remote.api.ActividadApi
import cl.duoc.mpazneb.data.remote.nuloSiNoExiste
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Pregunta

// Actividades y sus preguntas: ambas viven en el microservicio ms-actividades
class ActividadRepository(private val api: ActividadApi) {

    suspend fun filtrar(
        modulo: Modulo?,
        curso: Int?,
        dificultad: Dificultad?,
        estado: EstadoActividad?
    ): List<Actividad> =
        api.listar(modulo?.clave, curso, dificultad?.clave, estado?.clave).map { it.aModelo() }

    suspend fun buscarPorId(id: Long): Actividad? =
        nuloSiNoExiste { api.buscar(id) }?.aModelo()

    suspend fun crear(actividad: Actividad): Actividad =
        api.crear(actividad.aDto()).aModelo()

    suspend fun actualizar(actividad: Actividad): Actividad =
        api.actualizar(actividad.id, actividad.aDto()).aModelo()

    suspend fun preguntasDe(actividadId: Long): List<Pregunta> =
        api.preguntas(actividadId).map { it.aModelo() }

    suspend fun buscarPregunta(id: Long): Pregunta? =
        nuloSiNoExiste { api.buscarPregunta(id) }?.aModelo()

    // id = 0 significa pregunta nueva (POST); cualquier otro id es una edición (PUT)
    suspend fun guardarPregunta(pregunta: Pregunta): Pregunta =
        if (pregunta.id == 0L) {
            api.crearPregunta(pregunta.actividadId, pregunta.aDto()).aModelo()
        } else {
            api.actualizarPregunta(pregunta.id, pregunta.aDto()).aModelo()
        }

    suspend fun eliminarPregunta(id: Long) = api.eliminarPregunta(id)
}
