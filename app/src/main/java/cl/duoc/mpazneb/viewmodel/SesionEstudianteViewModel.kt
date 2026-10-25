package cl.duoc.mpazneb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import cl.duoc.mpazneb.MpazApplication
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.RespuestaRepository
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.model.Respuesta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class EtapaSesion {
    data object Instrucciones : EtapaSesion()
    data object Respondiendo : EtapaSesion()
    data object Retroalimentacion : EtapaSesion()
    data object Resultado : EtapaSesion()
}

data class SesionEstudianteUiState(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val nombreActividad: String = "",
    val instrucciones: String = "",
    val textoBase: String = "",
    val preguntas: List<Pregunta> = emptyList(),
    val etapa: EtapaSesion = EtapaSesion.Instrucciones,
    val indice: Int = 0,
    val opcionElegida: Int? = null,
    val confirmando: Boolean = false,
    val esCorrecta: Boolean = false,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val intento: Int = 1,
    val intentosPermitidos: Int = 1,
    val mensaje: String? = null
) {
    val preguntaActual: Pregunta? get() = preguntas.getOrNull(indice)
    val puedeReintentar: Boolean get() = intento < intentosPermitidos
}

class SesionEstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDeDatos = getApplication<MpazApplication>().baseDeDatos
    private val actividadRepo = ActividadRepository(baseDeDatos.actividadDao, baseDeDatos.preguntaDao)
    private val asignacionRepo = AsignacionRepository(baseDeDatos.asignacionDao)
    private val respuestaRepo = RespuestaRepository(baseDeDatos.respuestaDao)

    private val _uiState = MutableStateFlow(SesionEstudianteUiState())
    val uiState: StateFlow<SesionEstudianteUiState> = _uiState.asStateFlow()

    private var asignacion: Asignacion? = null
    private var cargado = false

    fun cargar(asignacionId: Long) {
        if (cargado) return
        cargado = true

        viewModelScope.launch {
            try {
                val encontrada = asignacionRepo.buscarPorId(asignacionId)
                val actividad = encontrada?.let { actividadRepo.buscarPorId(it.actividadId) }
                if (encontrada == null || actividad == null) {
                    _uiState.update { it.copy(cargando = false, mensaje = "No se pudo abrir la actividad") }
                    return@launch
                }

                val preguntas = actividadRepo.preguntasDe(actividad.id)
                if (preguntas.isEmpty()) {
                    _uiState.update {
                        it.copy(cargando = false, mensaje = "Esta actividad todavía no tiene preguntas")
                    }
                    return@launch
                }

                var actual = encontrada
                var etapa: EtapaSesion = EtapaSesion.Instrucciones
                var indice = 0

                when (encontrada.estado) {
                    EstadoAsignacion.Pendiente -> {
                        // Primera vez que entra: se inicia el intento 1
                        actual = encontrada.copy(
                            estado = EstadoAsignacion.EnDesarrollo,
                            intentos = 1,
                            aciertos = 0,
                            errores = 0
                        )
                        asignacionRepo.actualizar(actual)
                    }
                    EstadoAsignacion.EnDesarrollo -> {
                        // Retoma donde quedó: tantas preguntas respondidas como respuestas del intento
                        indice = respuestaRepo.deIntento(encontrada.id, encontrada.intentos).size
                        etapa = if (indice >= preguntas.size) EtapaSesion.Resultado else EtapaSesion.Respondiendo
                    }
                    EstadoAsignacion.Finalizada -> {
                        indice = preguntas.size
                        etapa = EtapaSesion.Resultado
                    }
                }

                asignacion = actual

                _uiState.update {
                    it.copy(
                        cargando = false,
                        nombreActividad = actividad.nombre,
                        instrucciones = actividad.instrucciones,
                        textoBase = actividad.textoBase,
                        preguntas = preguntas,
                        etapa = etapa,
                        indice = indice,
                        aciertos = actual.aciertos,
                        errores = actual.errores,
                        intento = actual.intentos,
                        intentosPermitidos = actividad.intentosPermitidos
                    )
                }

                // Si quedó en el resultado del último intento sin cerrarse, se cierra ahora
                if (etapa == EtapaSesion.Resultado && actual.intentos >= actividad.intentosPermitidos) {
                    finalizar()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, mensaje = "No se pudo abrir la actividad") }
            }
        }
    }

    fun comenzar() {
        _uiState.update { it.copy(etapa = EtapaSesion.Respondiendo) }
    }

    fun elegirOpcion(indice: Int) {
        _uiState.update { it.copy(opcionElegida = indice, mensaje = null) }
    }

    fun pedirEnviar() {
        if (_uiState.value.opcionElegida == null) {
            _uiState.update { it.copy(mensaje = "Elige una respuesta antes de enviar") }
        } else {
            _uiState.update { it.copy(confirmando = true, mensaje = null) }
        }
    }

    fun cancelarEnvio() {
        _uiState.update { it.copy(confirmando = false) }
    }

    fun confirmarEnvio() {
        val e = _uiState.value
        val pregunta = e.preguntaActual ?: return
        val elegida = e.opcionElegida ?: return
        val actual = asignacion ?: return
        if (e.guardando) return

        val acerto = elegida == pregunta.respuestaCorrecta
        val actualizada = actual.copy(
            aciertos = actual.aciertos + if (acerto) 1 else 0,
            errores = actual.errores + if (acerto) 0 else 1
        )

        _uiState.update { it.copy(confirmando = false, guardando = true) }

        viewModelScope.launch {
            try {
                // Las dos escrituras se guardan juntas o no se guarda ninguna
                baseDeDatos.withTransaction {
                    respuestaRepo.registrar(
                        Respuesta(
                            asignacionId = actual.id,
                            preguntaId = pregunta.id,
                            intento = e.intento,
                            opcionElegida = elegida,
                            esCorrecta = acerto
                        )
                    )
                    asignacionRepo.actualizar(actualizada)
                }
                asignacion = actualizada
                _uiState.update {
                    it.copy(
                        etapa = EtapaSesion.Retroalimentacion,
                        esCorrecta = acerto,
                        aciertos = actualizada.aciertos,
                        errores = actualizada.errores
                    )
                }
            } catch (ex: Exception) {
                _uiState.update { it.copy(mensaje = "No se pudo guardar tu respuesta. Intenta de nuevo") }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    fun siguiente() {
        val e = _uiState.value
        if (e.indice + 1 < e.preguntas.size) {
            _uiState.update {
                it.copy(indice = it.indice + 1, opcionElegida = null, etapa = EtapaSesion.Respondiendo)
            }
        } else {
            _uiState.update { it.copy(indice = it.preguntas.size, etapa = EtapaSesion.Resultado) }
            // Si no quedan intentos, la asignación se cierra y el código deja de servir
            if (!e.puedeReintentar) {
                viewModelScope.launch { finalizar() }
            }
        }
    }

    fun reintentar() {
        val actual = asignacion ?: return
        if (!_uiState.value.puedeReintentar) return

        val nueva = actual.copy(intentos = actual.intentos + 1, aciertos = 0, errores = 0)
        viewModelScope.launch {
            try {
                asignacionRepo.actualizar(nueva)
                asignacion = nueva
                _uiState.update {
                    it.copy(
                        intento = nueva.intentos,
                        indice = 0,
                        opcionElegida = null,
                        aciertos = 0,
                        errores = 0,
                        etapa = EtapaSesion.Respondiendo
                    )
                }
            } catch (ex: Exception) {
                _uiState.update { it.copy(mensaje = "No se pudo iniciar otro intento. Intenta de nuevo") }
            }
        }
    }

    // El estudiante decide terminar aunque le queden intentos
    fun terminar(alTerminar: () -> Unit) {
        viewModelScope.launch {
            finalizar()
            alTerminar()
        }
    }

    private suspend fun finalizar() {
        val actual = asignacion ?: return
        if (actual.estado == EstadoAsignacion.Finalizada) return
        val finalizada = actual.copy(estado = EstadoAsignacion.Finalizada)
        try {
            asignacionRepo.actualizar(finalizada)
            asignacion = finalizada
        } catch (ex: Exception) {
            _uiState.update { it.copy(mensaje = "No se pudo cerrar la actividad") }
        }
    }
}