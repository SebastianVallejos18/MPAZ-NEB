package cl.duoc.mpazneb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.util.etiquetaTipoPregunta
import cl.duoc.mpazneb.util.letraOpcion
import cl.duoc.mpazneb.viewmodel.PreguntasActividadViewModel

@Composable
fun PreguntasActividadScreen(
    actividadId: Long,
    onNuevaPregunta: () -> Unit,
    onEditarPregunta: (Long) -> Unit,
    onVolver: () -> Unit,
    viewModel: PreguntasActividadViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val preguntas by viewModel.preguntas.collectAsState()

    LaunchedEffect(actividadId) {
        viewModel.cargar(actividadId)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaPregunta,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva pregunta") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Preguntas", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = estado.nombreActividad,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            }

            estado.mensaje?.let { mensaje ->
                item {
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (preguntas.isEmpty()) {
                item {
                    Text(
                        text = "Esta actividad todavía no tiene preguntas. " +
                                "Agrega la primera con el botón de abajo.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            itemsIndexed(preguntas, key = { _, pregunta -> pregunta.id }) { indice, pregunta ->
                TarjetaPregunta(
                    numero = indice + 1,
                    pregunta = pregunta,
                    onEditar = { onEditarPregunta(pregunta.id) },
                    onEliminar = { viewModel.pedirEliminar(pregunta) }
                )
            }
        }
    }

    // Confirmación antes de borrar
    if (estado.preguntaPorEliminar != null) {
        AlertDialog(
            onDismissRequest = viewModel::cancelarEliminar,
            title = { Text("Eliminar pregunta") },
            text = { Text("¿Seguro que quieres eliminar esta pregunta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmarEliminar) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarEliminar) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaPregunta(
    numero: Int,
    pregunta: Pregunta,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Pregunta $numero · ${etiquetaTipoPregunta(pregunta.tipo)}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = pregunta.enunciado, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Respuesta correcta: ${letraOpcion(pregunta.respuestaCorrecta)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditar) {
                    Text("Editar")
                }
                TextButton(onClick = onEliminar) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}