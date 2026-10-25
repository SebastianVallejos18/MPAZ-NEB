package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.model.Asignacion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResumenAsignacion(
    val asignacion: Asignacion,
    val estudiante: String,
    val actividad: String,
    val totalPreguntas: Int
)

@OptIn(ExperimentalCoroutinesApi::class)
class ResultadosViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos
    private val actividadRepo = ActividadRepository(baseDeDatos.actividadDao, baseDeDatos.preguntaDao)
    private val usuarioRepo = UsuarioRepository(baseDeDatos.usuarioDao)
    private val asignacionRepo = AsignacionRepository(baseDeDatos.asignacionDao)

    private val _asistenteId = MutableStateFlow(0L)

    // Cada cambio en las asignaciones de la asistente vuelve a armar la lista
    val resumenes: StateFlow<List<ResumenAsignacion>> = _asistenteId
        .flatMapLatest { id -> asignacionRepo.observarDeAsistente(id) }
        .map { lista ->
            lista.map { asignacion ->
                ResumenAsignacion(
                    asignacion = asignacion,
                    estudiante = usuarioRepo.buscarPorId(asignacion.estudianteId)?.alias ?: "Estudiante",
                    actividad = actividadRepo.buscarPorId(asignacion.actividadId)?.nombre ?: "Actividad",
                    totalPreguntas = actividadRepo.preguntasDe(asignacion.actividadId).size
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cargar(asistenteId: Long) {
        _asistenteId.value = asistenteId
    }
}