package cl.duoc.mpazneb.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import cl.duoc.mpazneb.ui.components.BotonEscuchar
import cl.duoc.mpazneb.util.letraOpcion
import cl.duoc.mpazneb.util.opciones
import cl.duoc.mpazneb.util.textoParaLeer
import cl.duoc.mpazneb.viewmodel.EtapaSesion
import cl.duoc.mpazneb.viewmodel.SesionEstudianteUiState
import cl.duoc.mpazneb.viewmodel.SesionEstudianteViewModel

@Composable
fun SesionEstudianteScreen(
    asignacionId: Long,
    onSalir: () -> Unit,
    viewModel: SesionEstudianteViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val hablando by viewModel.hablando.collectAsState()
    val vozDisponible by viewModel.vozDisponible.collectAsState()
    val mostrarVoz = estado.vozActiva && vozDisponible

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                estado.cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                estado.preguntas.isEmpty() -> {
                    Text(
                        text = estado.mensaje ?: "No se pudo abrir la actividad",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onSalir, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("Salir")
                    }
                }

                else -> when (estado.etapa) {
                    EtapaSesion.Instrucciones -> VistaInstrucciones(
                        estado = estado,
                        mostrarVoz = mostrarVoz,
                        hablando = hablando,
                        onEscuchar = viewModel::alternarLectura,
                        onComenzar = viewModel::comenzar
                    )

                    EtapaSesion.Respondiendo, EtapaSesion.Retroalimentacion -> VistaPregunta(
                        estado = estado,
                        mostrarVoz = mostrarVoz,
                        hablando = hablando,
                        onEscuchar = viewModel::alternarLectura,
                        onElegir = viewModel::elegirOpcion,
                        onEnviar = viewModel::pedirEnviar,
                        onSiguiente = viewModel::siguiente
                    )

                    EtapaSesion.Resultado -> VistaResultado(
                        estado = estado,
                        onReintentar = viewModel::reintentar,
                        onTerminar = { viewModel.terminar(onSalir) },
                        onSalir = onSalir
                    )
                }
            }
        }
    }

    // Confirmación antes de enviar la respuesta
    if (estado.confirmando) {
        val pregunta = estado.preguntaActual
        val elegida = estado.opcionElegida
        if (pregunta != null && elegida != null) {
            AlertDialog(
                onDismissRequest = viewModel::cancelarEnvio,
                title = { Text("¿Enviar tu respuesta?") },
                text = {
                    Text(
                        text = "Elegiste la opción ${letraOpcion(elegida)}: ${pregunta.opciones()[elegida]}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmarEnvio) { Text("Sí, enviar") }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::cancelarEnvio) { Text("Cambiar") }
                }
            )
        }
    }
}

@Composable
private fun VistaInstrucciones(
    estado: SesionEstudianteUiState,
    mostrarVoz: Boolean,
    hablando: Boolean,
    onEscuchar: (String) -> Unit,
    onComenzar: () -> Unit
) {
    Text(text = estado.nombreActividad, style = MaterialTheme.typography.headlineMedium)
    Text(text = "Antes de empezar", style = MaterialTheme.typography.titleMedium)
    Text(text = estado.instrucciones, style = MaterialTheme.typography.bodyLarge)

    if (estado.textoBase.isNotBlank()) {
        Text(text = "Lee este texto con calma:", style = MaterialTheme.typography.titleMedium)
        TarjetaTexto(estado.textoBase)
    }

    Text(
        text = "Puedes intentarlo hasta ${estado.intentosPermitidos} " +
                if (estado.intentosPermitidos == 1) "vez." else "veces.",
        style = MaterialTheme.typography.bodyMedium
    )

    if (mostrarVoz) {
        BotonEscuchar(
            hablando = hablando,
            onClick = {
                onEscuchar(
                    listOf(estado.instrucciones, estado.textoBase)
                        .filter { it.isNotBlank() }
                        .joinToString(". ")
                )
            }
        )
    }

    Button(
        onClick = onComenzar,
        modifier = Modifier.fillMaxWidth().height(64.dp)
    ) {
        Text("Comenzar", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun VistaPregunta(
    estado: SesionEstudianteUiState,
    mostrarVoz: Boolean,
    hablando: Boolean,
    onEscuchar: (String) -> Unit,
    onElegir: (Int) -> Unit,
    onEnviar: () -> Unit,
    onSiguiente: () -> Unit
) {
    val pregunta = estado.preguntaActual ?: return
    val total = estado.preguntas.size
    val respondida = estado.etapa == EtapaSesion.Retroalimentacion

    // El avance sube de forma animada cuando se responde cada pregunta
    val avance = (estado.indice + if (respondida) 1 else 0) / total.toFloat()
    val progreso by animateFloatAsState(targetValue = avance, label = "progreso")

    Text(text = "Pregunta ${estado.indice + 1} de $total", style = MaterialTheme.typography.titleMedium)
    LinearProgressIndicator(
        progress = { progreso },
        modifier = Modifier.fillMaxWidth().height(10.dp)
    )

    if (estado.textoBase.isNotBlank()) {
        TarjetaTexto(estado.textoBase)
        if (mostrarVoz) {
            BotonEscuchar(hablando = hablando, onClick = { onEscuchar(estado.textoBase) })
        }
    }

    Text(text = pregunta.enunciado, style = MaterialTheme.typography.titleLarge)
    if (mostrarVoz) {
        BotonEscuchar(hablando = hablando, onClick = { onEscuchar(pregunta.textoParaLeer()) })
    }

    pregunta.opciones().forEachIndexed { indice, texto ->
        OpcionRespuesta(
            letra = letraOpcion(indice),
            texto = texto,
            seleccionada = estado.opcionElegida == indice,
            habilitada = !respondida,
            onClick = { onElegir(indice) }
        )
    }

    estado.mensaje?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }

    AnimatedVisibility(visible = respondida) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (estado.esCorrecta) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.tertiaryContainer
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (estado.esCorrecta) Icons.Filled.Check else Icons.Filled.Info,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (estado.esCorrecta) "¡Muy bien!" else "¡Buen intento!",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Text(text = pregunta.retroalimentacion, style = MaterialTheme.typography.bodyLarge)
                if (mostrarVoz) {
                    BotonEscuchar(hablando = hablando, onClick = { onEscuchar(pregunta.retroalimentacion) })
                }
                if (estado.esCorrecta && pregunta.procedimiento.isNotBlank()) {
                    Text(
                        text = "Así se resuelve: ${pregunta.procedimiento}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (!respondida) {
        Button(
            onClick = onEnviar,
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            if (estado.guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Enviar respuesta", style = MaterialTheme.typography.titleLarge)
            }
        }
    } else {
        Button(
            onClick = onSiguiente,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            val esUltima = estado.indice + 1 >= total
            Text(if (esUltima) "Ver mi resultado" else "Siguiente", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun VistaResultado(
    estado: SesionEstudianteUiState,
    onReintentar: () -> Unit,
    onTerminar: () -> Unit,
    onSalir: () -> Unit
) {
    Text(
        text = "¡Terminaste!",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Respondiste bien ${estado.aciertos} de ${estado.preguntas.size} preguntas",
        style = MaterialTheme.typography.titleLarge
    )
    Text(
        text = "Intento ${estado.intento} de ${estado.intentosPermitidos}",
        style = MaterialTheme.typography.bodyMedium
    )

    if (estado.puedeReintentar) {
        Text(
            text = "Si quieres, puedes intentarlo otra vez.",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onReintentar, modifier = Modifier.fillMaxWidth().height(64.dp)) {
            Text("Intentar de nuevo", style = MaterialTheme.typography.titleLarge)
        }
        OutlinedButton(onClick = onTerminar, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Terminar")
        }
    } else {
        Text(text = "¡Muy buen trabajo!", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onSalir, modifier = Modifier.fillMaxWidth().height(64.dp)) {
            Text("Salir", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun TarjetaTexto(texto: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun OpcionRespuesta(
    letra: String,
    texto: String,
    seleccionada: Boolean,
    habilitada: Boolean,
    onClick: () -> Unit
) {
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionada) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "colorOpcion"
    )

    Card(
        onClick = onClick,
        enabled = habilitada,
        colors = CardDefaults.cardColors(
            containerColor = colorFondo,
            disabledContainerColor = colorFondo
        ),
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = letra,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Text(text = texto, style = MaterialTheme.typography.titleMedium)
        }
    }
}