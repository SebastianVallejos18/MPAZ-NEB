package cl.duoc.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.di.AppModule
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.util.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FiltrosCatalogo(
    val modulo: Modulo? = null,
    val curso: Int? = null,
    val dificultad: Dificultad? = null,
    val estado: EstadoActividad? = null
)

data class ListaUiState(
    val cargando: Boolean = false,
    val error: String? = null
)

class CatalogoViewModel(
    private val repositorio: ActividadRepository = AppModule.actividadRepository
) : ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosCatalogo())
    val filtros: StateFlow<FiltrosCatalogo> = _filtros.asStateFlow()

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
    val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()

    private val _estado = MutableStateFlow(ListaUiState())
    val estado: StateFlow<ListaUiState> = _estado.asStateFlow()

    // La pantalla lo llama cada vez que aparece, así se ven los cambios hechos en el formulario
    fun recargar() {
        val f = _filtros.value
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                _actividades.value = repositorio.filtrar(f.modulo, f.curso, f.dificultad, f.estado)
            } catch (e: Exception) {
                _estado.update { it.copy(error = mensajeDeError(e, "No se pudieron cargar las actividades")) }
            } finally {
                _estado.update { it.copy(cargando = false) }
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

    fun filtrarPorEstado(estado: EstadoActividad?) {
        _filtros.update { it.copy(estado = estado) }
        recargar()
    }
}
