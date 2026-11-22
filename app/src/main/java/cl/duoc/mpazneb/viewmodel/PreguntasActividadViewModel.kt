package cl.duoc.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.di.AppModule
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.util.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PreguntasUiState(
    val nombreActividad: String = "",
    val estadoActividad: EstadoActividad = EstadoActividad.Borrador,
    val preguntaPorEliminar: Pregunta? = null,
    val mensaje: String? = null
)

class PreguntasActividadViewModel(
    private val repositorio: ActividadRepository = AppModule.actividadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreguntasUiState())
    val uiState: StateFlow<PreguntasUiState> = _uiState.asStateFlow()

    private val _preguntas = MutableStateFlow<List<Pregunta>>(emptyList())
    val preguntas: StateFlow<List<Pregunta>> = _preguntas.asStateFlow()

    private var actividadId = 0L

    // Se llama cada vez que la pantalla aparece (también al volver del formulario)
    fun cargar(idActividad: Long) {
        actividadId = idActividad
        viewModelScope.launch {
            try {
                val actividad = repositorio.buscarPorId(idActividad)
                if (actividad != null) {
                    _uiState.update {
                        it.copy(nombreActividad = actividad.nombre, estadoActividad = actividad.estado)
                    }
                }
                _preguntas.value = repositorio.preguntasDe(idActividad)
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = mensajeDeError(e, "No se pudieron cargar las preguntas")) }
            }
        }
    }

    fun pedirEliminar(pregunta: Pregunta) {
        val esLaUnica = _preguntas.value.size == 1
        if (esLaUnica && _uiState.value.estadoActividad == EstadoActividad.Disponible) {
            _uiState.update {
                it.copy(
                    mensaje = "No puedes eliminar la única pregunta de una actividad disponible. " +
                            "Cámbiala primero a borrador."
                )
            }
        } else {
            _uiState.update { it.copy(preguntaPorEliminar = pregunta, mensaje = null) }
        }
    }

    fun cancelarEliminar() {
        _uiState.update { it.copy(preguntaPorEliminar = null) }
    }

    fun confirmarEliminar() {
        val pregunta = _uiState.value.preguntaPorEliminar ?: return
        viewModelScope.launch {
            try {
                // DELETE en el microservicio y luego se vuelve a pedir la lista actualizada
                repositorio.eliminarPregunta(pregunta.id)
                _preguntas.value = repositorio.preguntasDe(actividadId)
                _uiState.update { it.copy(mensaje = "Pregunta eliminada") }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(mensaje = mensajeDeError(e, "No se pudo eliminar la pregunta"))
                }
            } finally {
                _uiState.update { it.copy(preguntaPorEliminar = null) }
            }
        }
    }
}
