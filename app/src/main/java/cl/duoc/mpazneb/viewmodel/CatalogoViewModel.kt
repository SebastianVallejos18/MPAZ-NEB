package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class FiltrosCatalogo(
    val modulo: Modulo? = null,
    val curso: Int? = null,
    val dificultad: Dificultad? = null,
    val estado: EstadoActividad? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos

    private val repositorio = ActividadRepository(
        baseDeDatos.actividadDao,
        baseDeDatos.preguntaDao
    )

    private val _filtros = MutableStateFlow(FiltrosCatalogo())
    val filtros: StateFlow<FiltrosCatalogo> = _filtros.asStateFlow()

    val actividades: StateFlow<List<Actividad>> = _filtros
        .flatMapLatest { f ->
            repositorio.filtrar(f.modulo, f.curso, f.dificultad, f.estado)
        }
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

    fun filtrarPorEstado(estado: EstadoActividad?) {
        _filtros.update { it.copy(estado = estado) }
    }
}