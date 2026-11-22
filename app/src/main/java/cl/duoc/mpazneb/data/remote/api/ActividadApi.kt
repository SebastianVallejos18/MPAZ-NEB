package cl.duoc.mpazneb.data.remote.api

import cl.duoc.mpazneb.data.remote.dto.ActividadDto
import cl.duoc.mpazneb.data.remote.dto.PreguntaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints del microservicio ms-actividades (puerto 8082)
interface ActividadApi {

    // Los filtros null no se envían en la URL
    @GET("api/actividades")
    suspend fun listar(
        @Query("modulo") modulo: String?,
        @Query("curso") curso: Int?,
        @Query("dificultad") dificultad: String?,
        @Query("estado") estado: String?
    ): List<ActividadDto>

    @GET("api/actividades/{id}")
    suspend fun buscar(@Path("id") id: Long): ActividadDto

    @POST("api/actividades")
    suspend fun crear(@Body actividad: ActividadDto): ActividadDto

    @PUT("api/actividades/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body actividad: ActividadDto): ActividadDto

    @GET("api/actividades/{id}/preguntas")
    suspend fun preguntas(@Path("id") actividadId: Long): List<PreguntaDto>

    @POST("api/actividades/{id}/preguntas")
    suspend fun crearPregunta(@Path("id") actividadId: Long, @Body pregunta: PreguntaDto): PreguntaDto

    @GET("api/preguntas/{id}")
    suspend fun buscarPregunta(@Path("id") id: Long): PreguntaDto

    @PUT("api/preguntas/{id}")
    suspend fun actualizarPregunta(@Path("id") id: Long, @Body pregunta: PreguntaDto): PreguntaDto

    // 204 sin cuerpo: se usa Unit
    @DELETE("api/preguntas/{id}")
    suspend fun eliminarPregunta(@Path("id") id: Long)
}
