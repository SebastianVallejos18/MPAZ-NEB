package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.RespuestaRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.util.letraOpcion
import cl.duoc.mpazneb.util.opciones
import cl.duoc.mpazneb.util.validarObservacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RespuestaDetalle(
    val intento: Int,
    val numeroPregunta: Int,
    val enunciado: String,
    val elegida: String,
    val esCorrecta: Boolean
)

data class DetalleUiState(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val estudiante: String = "",
    val actividad: String = "",
    val asignacion: Asignacion? = null,
    val totalPreguntas: Int = 0,
    val respuestas: List<RespuestaDetalle> = emptyList(),
    val observacion: String = "",
    val errorObservacion: String? = null,
    val mensaje: String? = null
)

class DetalleAsignacionViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos
    private val actividadRepo = ActividadRepository(baseDeDatos.actividadDao, baseDeDatos.preguntaDao)
    private val usuarioRepo = UsuarioRepository(baseDeDatos.usuarioDao)
    private val asignacionRepo = AsignacionRepository(baseDeDatos.asignacionDao)
    private val respuestaRepo = RespuestaRepository(baseDeDatos.respuestaDao)

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    private var cargado = false

    fun cargar(asignacionId: Long) {
        if (cargado) return
        cargado = true

        viewModelScope.launch {
            try {
                val asignacion = asignacionRepo.buscarPorId(asignacionId)
                val estudiante = asignacion?.let { usuarioRepo.buscarPorId(it.estudianteId) }
                val actividad = asignacion?.let { actividadRepo.buscarPorId(it.actividadId) }

                if (asignacion == null || estudiante == null || actividad == null) {
                    _uiState.update { it.copy(cargando = false, mensaje = "No se pudo abrir el detalle") }
                    return@launch
                }

                val preguntas = actividadRepo.preguntasDe(actividad.id)
                val preguntaPorId = preguntas.associateBy { it.id }

                val detalle = respuestaRepo.deAsignacion(asignacionId).mapNotNull { respuesta ->
                    val pregunta = preguntaPorId[respuesta.preguntaId] ?: return@mapNotNull null
                    RespuestaDetalle(
                        intento = respuesta.intento,
                        numeroPregunta = preguntas.indexOf(pregunta) + 1,
                        enunciado = pregunta.enunciado,
                        elegida = "${letraOpcion(respuesta.opcionElegida)}: " +
                                pregunta.opciones()[respuesta.opcionElegida],
                        esCorrecta = respuesta.esCorrecta
                    )
                }

                _uiState.update {
                    it.copy(
                        cargando = false,
                        estudiante = estudiante.alias,
                        actividad = actividad.nombre,
                        asignacion = asignacion,
                        totalPreguntas = preguntas.size,
                        respuestas = detalle,
                        observacion = asignacion.observacion
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, mensaje = "No se pudo abrir el detalle") }
            }
        }
    }

    fun onObservacionChange(valor: String) {
        _uiState.update {
            it.copy(
                observacion = valor,
                errorObservacion = if (it.errorObservacion != null) validarObservacion(valor) else null,
                mensaje = null
            )
        }
    }

    fun onGuardarObservacion() {
        val estado = _uiState.value
        val asignacion = estado.asignacion ?: return
        if (estado.guardando) return

        val error = validarObservacion(estado.observacion)
        if (error != null) {
            _uiState.update { it.copy(errorObservacion = error, mensaje = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }
            try {
                asignacionRepo.guardarObservacion(asignacion.id, estado.observacion.trim())
                _uiState.update { it.copy(mensaje = "Observación guardada") }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = "No se pudo guardar la observación") }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }
}