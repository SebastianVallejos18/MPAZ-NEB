package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.local.UsuarioDao
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrar(usuario: Usuario): Long = usuarioDao.insertar(usuario)

    suspend fun actualizar(usuario: Usuario) = usuarioDao.actualizar(usuario)

    suspend fun buscarPorAlias(alias: String): Usuario? = usuarioDao.buscarPorAlias(alias)

    suspend fun buscarPorId(id: Long): Usuario? = usuarioDao.buscarPorId(id)

    fun observarPorPerfil(perfil: Perfil): Flow<List<Usuario>> = usuarioDao.observarPorPerfil(perfil)

    suspend fun contar(): Int = usuarioDao.contar()
}