package cl.duoc.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.di.AppModule
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.util.mensajeDeError
import cl.duoc.mpazneb.util.validarAlias
import cl.duoc.mpazneb.util.validarClave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val alias: String = "",
    val clave: String = "",
    val errorAlias: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val usuarioIngresado: Usuario? = null
)

// El repositorio llega por constructor: en la app se usa el real (AppModule) y en las pruebas un mock
class LoginViewModel(
    private val repositorio: UsuarioRepository = AppModule.usuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onAliasChange(valor: String) {
        _uiState.update {
            it.copy(
                alias = valor,
                // Si ya había un error, se revalida mientras el usuario corrige
                errorAlias = if (it.errorAlias != null) validarAlias(valor) else null,
                errorGeneral = null
            )
        }
    }

    fun onClaveChange(valor: String) {
        _uiState.update {
            it.copy(
                clave = valor,
                errorClave = if (it.errorClave != null) validarClave(valor) else null,
                errorGeneral = null
            )
        }
    }

    fun onIngresar() {
        val estado = _uiState.value

        val errorAlias = validarAlias(estado.alias)
        val errorClave = validarClave(estado.clave)
        if (errorAlias != null || errorClave != null) {
            _uiState.update {
                it.copy(errorAlias = errorAlias, errorClave = errorClave, errorGeneral = null)
            }
            return
        }

        ejecutar {
            // El microservicio compara la contraseña y actualiza el último acceso
            val usuario = repositorio.login(estado.alias, estado.clave)

            if (usuario == null) {
                _uiState.update { it.copy(errorGeneral = "Alias o contraseña incorrectos") }
                return@ejecutar
            }

            if (usuario.perfil == Perfil.Estudiante) {
                _uiState.update {
                    it.copy(errorGeneral = "Los estudiantes ingresan con el código que entrega la asistente")
                }
                return@ejecutar
            }

            _uiState.update { it.copy(usuarioIngresado = usuario) }
        }
    }

    // Evita operaciones simultáneas y maneja el estado de carga
    private fun ejecutar(operacion: suspend () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.cargando) return@launch
            _uiState.update { it.copy(cargando = true) }
            try {
                operacion()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorGeneral = mensajeDeError(e, "Ocurrió un error. Intenta nuevamente"))
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
