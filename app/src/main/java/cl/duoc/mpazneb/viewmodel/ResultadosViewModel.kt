package cl.duoc.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.di.AppModule
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.util.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResumenAsignacion(
    val asignacion: Asignacion,
    val estudiante: String,
    val actividad: String,
    val totalPreguntas: Int
)

class ResultadosViewModel(
    private val actividadRepo: ActividadRepository = AppModule.actividadRepository,
    private val usuarioRepo: UsuarioRepository = AppModule.usuarioRepository,
    private val asignacionRepo: AsignacionRepository = AppModule.asignacionRepository
) : ViewModel() {

    private val _resumenes = MutableStateFlow<List<ResumenAsignacion>>(emptyList())
    val resumenes: StateFlow<List<ResumenAsignacion>> = _resumenes.asStateFlow()

    private val _estado = MutableStateFlow(ListaUiState())
    val estado: StateFlow<ListaUiState> = _estado.asStateFlow()

    // Junta datos de los tres microservicios: asignaciones + nombre del estudiante + nombre de la actividad
    fun cargar(asistenteId: Long) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                val asignaciones = asignacionRepo.deAsistente(asistenteId)

                // Se busca cada actividad y estudiante una sola vez, aunque se repitan
                val actividades = asignaciones.map { it.actividadId }.distinct()
                    .associateWith { id -> actividadRepo.buscarPorId(id) }
                // Si una actividad ya no existe (null), no se piden sus preguntas
                val totales = actividades.filterValues { it != null }.keys
                    .associateWith { id -> actividadRepo.preguntasDe(id).size }
                val estudiantes = asignaciones.map { it.estudianteId }.distinct()
                    .associateWith { id -> usuarioRepo.buscarPorId(id) }

                _resumenes.value = asignaciones.map { asignacion ->
                    ResumenAsignacion(
                        asignacion = asignacion,
                        estudiante = estudiantes[asignacion.estudianteId]?.alias ?: "Estudiante",
                        actividad = actividades[asignacion.actividadId]?.nombre ?: "Actividad",
                        totalPreguntas = totales[asignacion.actividadId] ?: 0
                    )
                }
            } catch (e: Exception) {
                _estado.update { it.copy(error = mensajeDeError(e, "No se pudieron cargar los resultados")) }
            } finally {
                _estado.update { it.copy(cargando = false) }
            }
        }
    }
}
