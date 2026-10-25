package cl.duoc.mpazneb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.ui.components.CampoTexto
import cl.duoc.mpazneb.util.etiquetaEstadoAsignacion
import cl.duoc.mpazneb.viewmodel.DetalleAsignacionViewModel
import cl.duoc.mpazneb.viewmodel.RespuestaDetalle

@Composable
fun DetalleAsignacionScreen(
    asignacionId: Long,
    onVolver: () -> Unit,
    viewModel: DetalleAsignacionViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(asignacionId) {
        viewModel.cargar(asignacionId)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Detalle", style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = onVolver) { Text("Volver") }
            }

            when {
                estado.cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                estado.asignacion == null -> {
                    Text(
                        text = estado.mensaje ?: "No se pudo abrir el detalle",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    val asignacion = estado.asignacion

                    Text(text = estado.estudiante, style = MaterialTheme.typography.titleLarge)
                    Text(text = estado.actividad, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Estado: ${etiquetaEstadoAsignacion(asignacion!!.estado)}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (estado.respuestas.isEmpty()) {
                        Text(
                            text = if (asignacion.estado == EstadoAsignacion.Pendiente) {
                                "El estudiante todavía no comienza la actividad."
                            } else {
                                "Todavía no hay respuestas registradas."
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Las respuestas se agrupan por intento
                    estado.respuestas.groupBy { it.intento }.forEach { (intento, lista) ->
                        val aciertos = lista.count { it.esCorrecta }
                        Text(
                            text = "Intento $intento · $aciertos de ${estado.totalPreguntas} aciertos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        lista.forEach { respuesta ->
                            TarjetaRespuesta(respuesta)
                        }
                    }

                    Text(
                        text = "Observación pedagógica",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Escribe una nota general que oriente la práctica. " +
                                "No incluyas nombres reales ni diagnósticos.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    CampoTexto(
                        valor = estado.observacion,
                        onValorChange = viewModel::onObservacionChange,
                        etiqueta = "Observación",
                        error = estado.errorObservacion,
                        minLineas = 3
                    )

                    estado.mensaje?.let {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(
                        onClick = viewModel::onGuardarObservacion,
                        enabled = !estado.guardando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (estado.guardando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar observación", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaRespuesta(respuesta: RespuestaDetalle) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Pregunta ${respuesta.numeroPregunta}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = respuesta.enunciado, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Respondió ${respuesta.elegida}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (respuesta.esCorrecta) "Acierto" else "Por reforzar",
                style = MaterialTheme.typography.labelLarge,
                color = if (respuesta.esCorrecta) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
            )
        }
    }
}