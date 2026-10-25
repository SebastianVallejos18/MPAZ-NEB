package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.util.tiposDePregunta
import cl.duoc.mpazneb.util.validarEnunciado
import cl.duoc.mpazneb.util.validarOpcion
import cl.duoc.mpazneb.util.validarOpcionesDistintas
import cl.duoc.mpazneb.util.validarProcedimiento
import cl.duoc.mpazneb.util.validarRetroalimentacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioPreguntaUiState(
    val preguntaId: Long = 0,
    val modulo: Modulo = Modulo.Lectura,
    val tipo: String = "EXPLICITA",
    val enunciado: String = "",
    val opcionA: String = "",
    val opcionB: String = "",
    val opcionC: String = "",
    val correcta: Int = 0,
    val procedimiento: String = "",
    val retroalimentacion: String = "",
    val errorEnunciado: String? = null,
    val errorOpcionA: String? = null,
    val errorOpcionB: String? = null,
    val errorOpcionC: String? = null,
    val errorOpciones: String? = null,
    val errorProcedimiento: String? = null,
    val errorRetroalimentacion: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val guardado: Boolean = false
)

class FormularioPreguntaViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos

    private val repositorio = ActividadRepository(
        baseDeDatos.actividadDao,
        baseDeDatos.preguntaDao
    )

    private val _uiState = MutableStateFlow(FormularioPreguntaUiState())
    val uiState: StateFlow<FormularioPreguntaUiState> = _uiState.asStateFlow()

    private var actividadId = 0L
    private var cargado = false

    // idPregunta = 0 significa "pregunta nueva"
    fun cargar(idActividad: Long, idPregunta: Long) {
        if (cargado) return
        cargado = true
        actividadId = idActividad

        ejecutar {
            val actividad = repositorio.buscarPorId(idActividad) ?: return@ejecutar
            val pregunta = if (idPregunta != 0L) repositorio.buscarPregunta(idPregunta) else null

            _uiState.update {
                if (pregunta == null) {
                    it.copy(
                        modulo = actividad.modulo,
                        tipo = tiposDePregunta(actividad.modulo).first()
                    )
                } else {
                    it.copy(
                        preguntaId = pregunta.id,
                        modulo = actividad.modulo,
                        tipo = pregunta.tipo,
                        enunciado = pregunta.enunciado,
                        opcionA = pregunta.opcionA,
                        opcionB = pregunta.opcionB,
                        opcionC = pregunta.opcionC,
                        correcta = pregunta.respuestaCorrecta,
                        procedimiento = pregunta.procedimiento,
                        retroalimentacion = pregunta.retroalimentacion
                    )
                }
            }
        }
    }

    fun onTipoChange(valor: String) = _uiState.update { it.copy(tipo = valor) }

    fun onCorrectaChange(valor: Int) = _uiState.update { it.copy(correcta = valor) }

    fun onEnunciadoChange(valor: String) = _uiState.update {
        it.copy(
            enunciado = valor,
            errorEnunciado = if (it.errorEnunciado != null) validarEnunciado(valor) else null
        )
    }

    fun onOpcionAChange(valor: String) = _uiState.update {
        it.copy(
            opcionA = valor,
            errorOpcionA = if (it.errorOpcionA != null) validarOpcion(valor) else null,
            errorOpciones = null
        )
    }

    fun onOpcionBChange(valor: String) = _uiState.update {
        it.copy(
            opcionB = valor,
            errorOpcionB = if (it.errorOpcionB != null) validarOpcion(valor) else null,
            errorOpciones = null
        )
    }

    fun onOpcionCChange(valor: String) = _uiState.update {
        it.copy(
            opcionC = valor,
            errorOpcionC = if (it.errorOpcionC != null) validarOpcion(valor) else null,
            errorOpciones = null
        )
    }

    fun onProcedimientoChange(valor: String) = _uiState.update {
        it.copy(
            procedimiento = valor,
            errorProcedimiento = if (it.errorProcedimiento != null) validarProcedimiento(it.modulo, valor) else null
        )
    }

    fun onRetroalimentacionChange(valor: String) = _uiState.update {
        it.copy(
            retroalimentacion = valor,
            errorRetroalimentacion = if (it.errorRetroalimentacion != null) validarRetroalimentacion(valor) else null
        )
    }

    fun onGuardar() {
        val e = _uiState.value

        val errorEnunciado = validarEnunciado(e.enunciado)
        val errorA = validarOpcion(e.opcionA)
        val errorB = validarOpcion(e.opcionB)
        val errorC = validarOpcion(e.opcionC)
        // Solo se revisa que sean distintas si cada una ya es válida por separado
        val errorOpciones = if (errorA == null && errorB == null && errorC == null) {
            validarOpcionesDistintas(e.opcionA, e.opcionB, e.opcionC)
        } else null
        val errorProcedimiento = validarProcedimiento(e.modulo, e.procedimiento)
        val errorRetroalimentacion = validarRetroalimentacion(e.retroalimentacion)

        val hayErrores = listOf(
            errorEnunciado, errorA, errorB, errorC,
            errorOpciones, errorProcedimiento, errorRetroalimentacion
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorEnunciado = errorEnunciado,
                    errorOpcionA = errorA,
                    errorOpcionB = errorB,
                    errorOpcionC = errorC,
                    errorOpciones = errorOpciones,
                    errorProcedimiento = errorProcedimiento,
                    errorRetroalimentacion = errorRetroalimentacion,
                    errorGeneral = null
                )
            }
            return
        }

        ejecutar {
            repositorio.guardarPregunta(
                Pregunta(
                    id = e.preguntaId,
                    actividadId = actividadId,
                    tipo = e.tipo,
                    enunciado = e.enunciado.trim(),
                    opcionA = e.opcionA.trim(),
                    opcionB = e.opcionB.trim(),
                    opcionC = e.opcionC.trim(),
                    respuestaCorrecta = e.correcta,
                    procedimiento = if (e.modulo == Modulo.Matematica) e.procedimiento.trim() else "",
                    retroalimentacion = e.retroalimentacion.trim()
                )
            )
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