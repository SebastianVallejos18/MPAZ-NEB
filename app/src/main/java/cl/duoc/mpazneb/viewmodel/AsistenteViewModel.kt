package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.util.generarCodigo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AsistenteUiState(
    val actividadPorAsignar: Actividad? = null,
    val estudianteElegido: Usuario? = null,
    val errorAsignacion: String? = null,
    val codigoGenerado: String? = null,
    val aliasAsignado: String? = null,
    val cargando: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class AsistenteViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos

    private val actividadRepo = ActividadRepository(
        baseDeDatos.actividadDao,
        baseDeDatos.preguntaDao
    )
    private val usuarioRepo = UsuarioRepository(baseDeDatos.usuarioDao)
    private val asignacionRepo = AsignacionRepository(baseDeDatos.asignacionDao)

    private val _filtros = MutableStateFlow(FiltrosCatalogo())
    val filtros: StateFlow<FiltrosCatalogo> = _filtros.asStateFlow()

    private val _uiState = MutableStateFlow(AsistenteUiState())
    val uiState: StateFlow<AsistenteUiState> = _uiState.asStateFlow()

    // Solo actividades disponibles: la asistente nunca ve borradores ni desactivadas
    val actividades: StateFlow<List<Actividad>> = _filtros
        .flatMapLatest { f ->
            actividadRepo.filtrar(f.modulo, f.curso, f.dificultad, EstadoActividad.Disponible)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val estudiantes: StateFlow<List<Usuario>> = usuarioRepo
        .observarPorPerfil(Perfil.Estudiante)
        .map { lista -> lista.filter { it.activo } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun filtrarPorModulo(modulo: Modulo?) {
        _filtros.update { it.copy(modulo = modulo) }
    }

    fun filtrarPorCurso(curso: Int?) {
        _filtros.update { it.copy(curso = curso) }
    }

    fun filtrarPorDificultad(dificultad: Dificultad?) {
        _filtros.update { it.copy(dificultad = dificultad) }
    }

    fun pedirAsignar(actividad: Actividad) {
        _uiState.update {
            it.copy(actividadPorAsignar = actividad, estudianteElegido = null, errorAsignacion = null)
        }
    }

    fun elegirEstudiante(estudiante: Usuario) {
        _uiState.update { it.copy(estudianteElegido = estudiante, errorAsignacion = null) }
    }

    fun cancelarAsignacion() {
        _uiState.update {
            it.copy(actividadPorAsignar = null, estudianteElegido = null, errorAsignacion = null)
        }
    }

    fun cerrarCodigo() {
        _uiState.update { it.copy(codigoGenerado = null, aliasAsignado = null) }
    }

    fun confirmarAsignacion(asistenteId: Long) {
        val estado = _uiState.value
        val actividad = estado.actividadPorAsignar ?: return
        val estudiante = estado.estudianteElegido

        if (estudiante == null) {
            _uiState.update { it.copy(errorAsignacion = "Elige un estudiante para continuar") }
            return
        }

        viewModelScope.launch {
            if (_uiState.value.cargando) return@launch
            _uiState.update { it.copy(cargando = true) }
            try {
                // Se repite hasta encontrar un código que no exista todavía
                var codigo = generarCodigo()
                while (asignacionRepo.buscarPorCodigo(codigo) != null) {
                    codigo = generarCodigo()
                }

                asignacionRepo.crear(
                    Asignacion(
                        actividadId = actividad.id,
                        estudianteId = estudiante.id,
                        asistenteId = asistenteId,
                        codigoTemporal = codigo
                    )
                )

                _uiState.update {
                    it.copy(
                        actividadPorAsignar = null,
                        estudianteElegido = null,
                        codigoGenerado = codigo,
                        aliasAsignado = estudiante.alias
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorAsignacion = "No se pudo crear la asignación. Intenta nuevamente")
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}