package cl.duoc.mpazneb.data.remote.api

import cl.duoc.mpazneb.data.remote.dto.AsignacionDto
import cl.duoc.mpazneb.data.remote.dto.CrearAsignacionRequest
import cl.duoc.mpazneb.data.remote.dto.ObservacionRequest
import cl.duoc.mpazneb.data.remote.dto.ProgresoRequest
import cl.duoc.mpazneb.data.remote.dto.RespuestaDto
import cl.duoc.mpazneb.data.remote.dto.RespuestaRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints del microservicio ms-asignaciones (puerto 8083)
interface AsignacionApi {

    @GET("api/asignaciones")
    suspend fun deAsistente(@Query("asistenteId") asistenteId: Long): List<AsignacionDto>

    @GET("api/asignaciones/{id}")
    suspend fun buscar(@Path("id") id: Long): AsignacionDto

    @GET("api/asignaciones/codigo/{codigo}")
    suspend fun buscarPorCodigo(@Path("codigo") codigo: String): AsignacionDto

    @POST("api/asignaciones")
    suspend fun crear(@Body datos: CrearAsignacionRequest): AsignacionDto

    @PUT("api/asignaciones/{id}")
    suspend fun actualizarProgreso(@Path("id") id: Long, @Body datos: ProgresoRequest): AsignacionDto

    @PATCH("api/asignaciones/{id}/observacion")
    suspend fun guardarObservacion(@Path("id") id: Long, @Body datos: ObservacionRequest): AsignacionDto

    @DELETE("api/asignaciones/{id}")
    suspend fun eliminar(@Path("id") id: Long)

    @GET("api/asignaciones/{id}/respuestas")
    suspend fun respuestas(@Path("id") asignacionId: Long, @Query("intento") intento: Int?): List<RespuestaDto>

    @POST("api/asignaciones/{id}/respuestas")
    suspend fun registrarRespuesta(@Path("id") asignacionId: Long, @Body datos: RespuestaRequest): AsignacionDto
}
