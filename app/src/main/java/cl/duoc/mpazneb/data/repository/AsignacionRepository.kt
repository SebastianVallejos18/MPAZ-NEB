package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.remote.aModelo
import cl.duoc.mpazneb.data.remote.aProgreso
import cl.duoc.mpazneb.data.remote.aRequest
import cl.duoc.mpazneb.data.remote.api.AsignacionApi
import cl.duoc.mpazneb.data.remote.dto.CrearAsignacionRequest
import cl.duoc.mpazneb.data.remote.dto.ObservacionRequest
import cl.duoc.mpazneb.data.remote.nuloSiNoExiste
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.Respuesta

// Asignaciones y respuestas: ambas viven en el microservicio ms-asignaciones
class AsignacionRepository(private val api: AsignacionApi) {

    // El código temporal lo genera el microservicio y viene en la respuesta
    suspend fun crear(actividadId: Long, estudianteId: Long, asistenteId: Long): Asignacion =
        api.crear(CrearAsignacionRequest(actividadId, estudianteId, asistenteId)).aModelo()

    suspend fun buscarPorId(id: Long): Asignacion? =
        nuloSiNoExiste { api.buscar(id) }?.aModelo()

    suspend fun buscarPorCodigo(codigo: String): Asignacion? =
        nuloSiNoExiste { api.buscarPorCodigo(codigo) }?.aModelo()

    suspend fun deAsistente(asistenteId: Long): List<Asignacion> =
        api.deAsistente(asistenteId).map { it.aModelo() }

    // Guarda estado, intentos, aciertos y errores; devuelve la asignación como quedó en el servidor
    suspend fun actualizarProgreso(asignacion: Asignacion): Asignacion =
        api.actualizarProgreso(asignacion.id, asignacion.aProgreso()).aModelo()

    suspend fun guardarObservacion(id: Long, observacion: String): Asignacion =
        api.guardarObservacion(id, ObservacionRequest(observacion)).aModelo()

    suspend fun eliminar(id: Long) = api.eliminar(id)

    // intento = null trae las respuestas de todos los intentos
    suspend fun respuestasDe(asignacionId: Long, intento: Int? = null): List<Respuesta> =
        api.respuestas(asignacionId, intento).map { it.aModelo() }

    // El microservicio guarda la respuesta y suma el acierto o error en una sola transacción
    suspend fun registrarRespuesta(respuesta: Respuesta): Asignacion =
        api.registrarRespuesta(respuesta.asignacionId, respuesta.aRequest()).aModelo()
}
