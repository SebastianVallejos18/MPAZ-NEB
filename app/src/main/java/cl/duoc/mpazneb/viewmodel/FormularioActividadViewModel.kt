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
import cl.duoc.mpazneb.util.validarContenido
import cl.duoc.mpazneb.util.validarEstado
import cl.duoc.mpazneb.util.validarInstrucciones
import cl.duoc.mpazneb.util.validarIntentos
import cl.duoc.mpazneb.util.validarNombreActividad
import cl.duoc.mpazneb.util.validarObjetivo
import cl.duoc.mpazneb.util.validarTextoBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioActividadUiState(
    val actividadId: Long = 0,
    val nombre: String = "",
    val modulo: Modulo = Modulo.Lectura,
    val curso: Int = 1,
    val contenido: String = "",
    val objetivo: String = "",
    val dificultad: Dificultad = Dificultad.Facil,
    val instrucciones: String = "",
    val textoBase: String = "",
    val intentos: String = "1",
    val estado: EstadoActividad = EstadoActividad.Borrador,
    val cantidadPreguntas: Int = 0,
    val errorNombre: String? = null,
    val errorContenido: String? = null,
    val errorObjetivo: String? = null,
    val errorInstrucciones: String? = null,
    val errorTextoBase: String? = null,
    val errorIntentos: String? = null,
    val errorEstado: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val guardado: Boolean = false,
    val idCreado: Long? = null
)

class FormularioActividadViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos

    private val repositorio = ActividadRepository(
        baseDeDatos.actividadDao,
        baseDeDatos.preguntaDao
    )

    private val _uiState = MutableStateFlow(FormularioActividadUiState())
    val uiState: StateFlow<FormularioActividadUiState> = _uiState.asStateFlow()

    private var cargado = false

    // id = 0 significa "actividad nueva"; cualquier otro id significa "editar"
    fun cargar(actividadId: Long) {
        if (cargado) return
        cargado = true
        if (actividadId == 0L) return

        ejecutar {
            val actividad = repositorio.buscarPorId(actividadId) ?: return@ejecutar
            val cantidad = repositorio.preguntasDe(actividadId).size
            _uiState.update {
                it.copy(
                    actividadId = actividad.id,
                    nombre = actividad.nombre,
                    modulo = actividad.modulo,
                    curso = actividad.cursoRecomendado,
                    contenido = actividad.contenido,
                    objetivo = actividad.objetivoPedagogico,
                    dificultad = actividad.dificultad,
                    instrucciones = actividad.instrucciones,
                    textoBase = actividad.textoBase,
                    intentos = actividad.intentosPermitidos.toString(),
                    estado = actividad.estado,
                    cantidadPreguntas = cantidad
                )
            }
        }
    }

    // Si un campo ya tenía error, se revalida mientras el usuario corrige
    fun onNombreChange(valor: String) = _uiState.update {
        it.copy(
            nombre = valor,
            errorNombre = if (it.errorNombre != null) validarNombreActividad(valor) else null
        )
    }

    fun onContenidoChange(valor: String) = _uiState.update {
        it.copy(
            contenido = valor,
            errorContenido = if (it.errorContenido != null) validarContenido(valor) else null
        )
    }

    fun onObjetivoChange(valor: String) = _uiState.update {
        it.copy(
            objetivo = valor,
            errorObjetivo = if (it.errorObjetivo != null) validarObjetivo(valor) else null
        )
    }

    fun onInstruccionesChange(valor: String) = _uiState.update {
        it.copy(
            instrucciones = valor,
            errorInstrucciones = if (it.errorInstrucciones != null) validarInstrucciones(valor) else null
        )
    }

    fun onTextoBaseChange(valor: String) = _uiState.update {
        it.copy(
            textoBase = valor,
            errorTextoBase = if (it.errorTextoBase != null) validarTextoBase(it.modulo, valor) else null
        )
    }

    fun onIntentosChange(valor: String) = _uiState.update {
        it.copy(
            intentos = valor,
            errorIntentos = if (it.errorIntentos != null) validarIntentos(valor) else null
        )
    }

    fun onModuloChange(valor: Modulo) = _uiState.update {
        it.copy(modulo = valor, errorTextoBase = null)
    }

    fun onCursoChange(valor: Int) = _uiState.update { it.copy(curso = valor) }

    fun onDificultadChange(valor: Dificultad) = _uiState.update { it.copy(dificultad = valor) }

    fun onEstadoChange(valor: EstadoActividad) = _uiState.update {
        it.copy(
            estado = valor,
            errorEstado = if (it.errorEstado != null) validarEstado(valor, it.cantidadPreguntas) else null
        )
    }

    fun onGuardar() {
        val e = _uiState.value

        val errorNombre = validarNombreActividad(e.nombre)
        val errorContenido = validarContenido(e.contenido)
        val errorObjetivo = validarObjetivo(e.objetivo)
        val errorInstrucciones = validarInstrucciones(e.instrucciones)
        val errorTextoBase = validarTextoBase(e.modulo, e.textoBase)
        val errorIntentos = validarIntentos(e.intentos)
        val errorEstado = validarEstado(e.estado, e.cantidadPreguntas)

        val hayErrores = listOf(
            errorNombre, errorContenido, errorObjetivo, errorInstrucciones,
            errorTextoBase, errorIntentos, errorEstado
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombre = errorNombre,
                    errorContenido = errorContenido,
                    errorObjetivo = errorObjetivo,
                    errorInstrucciones = errorInstrucciones,
                    errorTextoBase = errorTextoBase,
                    errorIntentos = errorIntentos,
                    errorEstado = errorEstado,
                    errorGeneral = null
                )
            }
            return
        }

        ejecutar {
            val actividad = Actividad(
                id = e.actividadId,
                nombre = e.nombre.trim(),
                modulo = e.modulo,
                cursoRecomendado = e.curso,
                contenido = e.contenido.trim(),
                objetivoPedagogico = e.objetivo.trim(),
                dificultad = e.dificultad,
                instrucciones = e.instrucciones.trim(),
                textoBase = if (e.modulo == Modulo.Lectura) e.textoBase.trim() else "",
                intentosPermitidos = e.intentos.toInt(),
                estado = e.estado
            )

            if (e.actividadId == 0L) {
                val idNuevo = repositorio.crear(actividad, emptyList())
                _uiState.update { it.copy(idCreado = idNuevo) }
            } else {
                repositorio.actualizar(actividad)
                _uiState.update { it.copy(guardado = true) }
            }
            _uiState.update { it.copy(guardado = true) }
        }
    }

    private fun ejecutar(operacion: suspend () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.cargando) return@launch
            _uiState.update { it.copy(cargando = true) }
            try {
                operacion()
            } catch (ex: Exception) {
                _uiState.update { it.copy(errorGeneral = "No se pudo guardar. Intenta nuevamente") }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}