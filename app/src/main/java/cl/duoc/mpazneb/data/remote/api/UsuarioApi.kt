package cl.duoc.mpazneb.data.remote.api

import cl.duoc.mpazneb.data.remote.dto.LoginRequest
import cl.duoc.mpazneb.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints del microservicio ms-usuarios (puerto 8081)
interface UsuarioApi {

    @POST("api/usuarios/login")
    suspend fun login(@Body datos: LoginRequest): UsuarioDto

    @GET("api/usuarios")
    suspend fun listar(@Query("perfil") perfil: String?): List<UsuarioDto>

    @GET("api/usuarios/{id}")
    suspend fun buscar(@Path("id") id: Long): UsuarioDto

    @PATCH("api/usuarios/{id}/acceso")
    suspend fun registrarAcceso(@Path("id") id: Long): UsuarioDto
}
