package cl.duoc.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.di.AppModule
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.util.mensajeDeError
import cl.duoc.mpazneb.util.validarCodigo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IngresoEstudianteUiState(
    val codigo: String = "",
    val errorCodigo: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val estudiante: Usuario? = null,
    val asignacionId: Long? = null
)

class IngresoEstudianteViewModel(
    private val usuarioRepo: UsuarioRepository = AppModule.usuarioRepository,
    private val asignacionRepo: AsignacionRepository = AppModule.asignacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngresoEstudianteUiState())
    val uiState: StateFlow<IngresoEstudianteUiState> = _uiState.asStateFlow()

    fun onCodigoChange(valor: String) {
        // Solo letras y números, máximo 6, siempre en mayúscula
        val limpio = valor.filter { it.isLetterOrDigit() }.take(6).uppercase()
        _uiState.update { it.copy(codigo = limpio, errorCodigo = null, errorGeneral = null) }
    }

    fun onIngresar() {
        val codigo = _uiState.value.codigo

        val errorCodigo = validarCodigo(codigo)
        if (errorCodigo != null) {
            _uiState.update { it.copy(errorCodigo = errorCodigo, errorGeneral = null) }
            return
        }

        ejecutar {
            // Primero se busca el código en ms-asignaciones y luego el estudiante en ms-usuarios
            val asignacion = asignacionRepo.buscarPorCodigo(codigo)
            val estudiante = asignacion?.let { usuarioRepo.buscarPorId(it.estudianteId) }

            if (asignacion == null || estudiante == null || !estudiante.activo) {
                _uiState.update {
                    it.copy(errorGeneral = "No encontramos ese código. Revisa que esté bien escrito")
                }
                return@ejecutar
            }

            if (asignacion.estado == EstadoAsignacion.Finalizada) {
                _uiState.update {
                    it.copy(errorGeneral = "Esta actividad ya terminó. Pide un código nuevo a tu asistente")
                }
                return@ejecutar
            }

            val actualizado = usuarioRepo.registrarAcceso(estudiante.id)
            _uiState.update { it.copy(estudiante = actualizado, asignacionId = asignacion.id) }
        }
    }

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
