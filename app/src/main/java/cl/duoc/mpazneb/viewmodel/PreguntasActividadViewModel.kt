package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Pregunta
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PreguntasUiState(
    val nombreActividad: String = "",
    val estadoActividad: EstadoActividad = EstadoActividad.Borrador,
    val preguntaPorEliminar: Pregunta? = null,
    val mensaje: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class PreguntasActividadViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos

    private val repositorio = ActividadRepository(
        baseDeDatos.actividadDao,
        baseDeDatos.preguntaDao
    )

    private val _uiState = MutableStateFlow(PreguntasUiState())
    val uiState: StateFlow<PreguntasUiState> = _uiState.asStateFlow()

    private val _actividadId = MutableStateFlow(0L)

    val preguntas: StateFlow<List<Pregunta>> = _actividadId
        .flatMapLatest { id -> repositorio.observarPreguntas(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cargar(actividadId: Long) {
        if (_actividadId.value == actividadId) return
        _actividadId.value = actividadId
        viewModelScope.launch {
            val actividad = repositorio.buscarPorId(actividadId) ?: return@launch
            _uiState.update {
                it.copy(nombreActividad = actividad.nombre, estadoActividad = actividad.estado)
            }
        }
    }

    fun pedirEliminar(pregunta: Pregunta) {
        val esLaUnica = preguntas.value.size == 1
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
                repositorio.eliminarPregunta(pregunta)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(mensaje = "No se pudo eliminar la pregunta. Puede tener respuestas registradas")
                }
            } finally {
                _uiState.update { it.copy(preguntaPorEliminar = null) }
            }
        }
    }
}