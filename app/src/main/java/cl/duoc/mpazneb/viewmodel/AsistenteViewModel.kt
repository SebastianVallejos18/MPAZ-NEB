package cl.duoc.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.di.AppModule
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.util.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class AsistenteViewModel(
    private val actividadRepo: ActividadRepository = AppModule.actividadRepository,
    private val usuarioRepo: UsuarioRepository = AppModule.usuarioRepository,
    private val asignacionRepo: AsignacionRepository = AppModule.asignacionRepository
) : ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosCatalogo())
    val filtros: StateFlow<FiltrosCatalogo> = _filtros.asStateFlow()

    private val _uiState = MutableStateFlow(AsistenteUiState())
    val uiState: StateFlow<AsistenteUiState> = _uiState.asStateFlow()

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
    val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()

    private val _estudiantes = MutableStateFlow<List<Usuario>>(emptyList())
    val estudiantes: StateFlow<List<Usuario>> = _estudiantes.asStateFlow()

    private val _estadoLista = MutableStateFlow(ListaUiState())
    val estadoLista: StateFlow<ListaUiState> = _estadoLista.asStateFlow()

    fun recargar() {
        val f = _filtros.value
        viewModelScope.launch {
            _estadoLista.update { it.copy(cargando = true, error = null) }
            try {
                // Solo actividades disponibles: la asistente nunca ve borradores ni desactivadas
                _actividades.value = actividadRepo.filtrar(
                    f.modulo, f.curso, f.dificultad, EstadoActividad.Disponible
                )
                _estudiantes.value = usuarioRepo.listarPorPerfil(Perfil.Estudiante).filter { it.activo }
            } catch (e: Exception) {
                _estadoLista.update {
                    it.copy(error = mensajeDeError(e, "No se pudieron cargar las actividades"))
                }
            } finally {
                _estadoLista.update { it.copy(cargando = false) }
            }
        }
    }

    fun filtrarPorModulo(modulo: Modulo?) {
        _filtros.update { it.copy(modulo = modulo) }
        recargar()
    }

    fun filtrarPorCurso(curso: Int?) {
        _filtros.update { it.copy(curso = curso) }
        recargar()
    }

    fun filtrarPorDificultad(dificultad: Dificultad?) {
        _filtros.update { it.copy(dificultad = dificultad) }
        recargar()
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
                // El microservicio genera un código único y lo devuelve en la asignación creada
                val asignacion = asignacionRepo.crear(actividad.id, estudiante.id, asistenteId)

                _uiState.update {
                    it.copy(
                        actividadPorAsignar = null,
                        estudianteElegido = null,
                        codigoGenerado = asignacion.codigoTemporal,
                        aliasAsignado = estudiante.alias
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorAsignacion = mensajeDeError(e, "No se pudo crear la asignación. Intenta nuevamente"))
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
