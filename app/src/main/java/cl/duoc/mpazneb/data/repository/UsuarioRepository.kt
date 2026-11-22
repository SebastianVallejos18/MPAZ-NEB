package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.remote.aModelo
import cl.duoc.mpazneb.data.remote.api.UsuarioApi
import cl.duoc.mpazneb.data.remote.dto.LoginRequest
import cl.duoc.mpazneb.data.remote.nuloSiNoExiste
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Usuario
import retrofit2.HttpException

// Único punto de acceso a los usuarios. El ViewModel no sabe si los datos vienen de internet o de otro lado.
class UsuarioRepository(private val api: UsuarioApi) {

    // Devuelve null si el alias o la contraseña no coinciden (el microservicio responde 401)
    suspend fun login(alias: String, clave: String): Usuario? =
        try {
            api.login(LoginRequest(alias, clave)).aModelo()
        } catch (e: HttpException) {
            if (e.code() == 401) null else throw e
        }

    suspend fun buscarPorId(id: Long): Usuario? =
        nuloSiNoExiste { api.buscar(id) }?.aModelo()

    suspend fun listarPorPerfil(perfil: Perfil): List<Usuario> =
        api.listar(perfil.clave).map { it.aModelo() }

    suspend fun registrarAcceso(id: Long): Usuario =
        api.registrarAcceso(id).aModelo()
}
