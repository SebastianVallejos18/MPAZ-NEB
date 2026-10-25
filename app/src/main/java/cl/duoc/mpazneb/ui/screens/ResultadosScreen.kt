package cl.duoc.mpazneb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import cl.duoc.mpazneb.util.etiquetaEstadoAsignacion
import cl.duoc.mpazneb.util.formatearFecha
import cl.duoc.mpazneb.viewmodel.ResultadosViewModel
import cl.duoc.mpazneb.viewmodel.ResumenAsignacion

@Composable
fun ResultadosScreen(
    asistenteId: Long,
    onVerDetalle: (Long) -> Unit,
    onVolver: () -> Unit,
    viewModel: ResultadosViewModel = viewModel()
) {
    val resumenes by viewModel.resumenes.collectAsState()

    LaunchedEffect(asistenteId) {
        viewModel.cargar(asistenteId)
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Resultados", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "Respuestas e intentos de tus asignaciones",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            }

            if (resumenes.isEmpty()) {
                item {
                    Text(
                        text = "Todavía no has asignado actividades.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(resumenes, key = { it.asignacion.id }) { resumen ->
                TarjetaResumen(resumen = resumen, onClick = { onVerDetalle(resumen.asignacion.id) })
            }
        }
    }
}

@Composable
private fun TarjetaResumen(resumen: ResumenAsignacion, onClick: () -> Unit) {
    val asignacion = resumen.asignacion

    val progreso = if (asignacion.estado == EstadoAsignacion.Pendiente) {
        "Aún no comienza"
    } else {
        "Intento ${asignacion.intentos} · ${asignacion.aciertos} de ${resumen.totalPreguntas} aciertos"
    }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = resumen.estudiante, style = MaterialTheme.typography.titleMedium)
            Text(text = resumen.actividad, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${etiquetaEstadoAsignacion(asignacion.estado)} · $progreso",
                style = MaterialTheme.typography.bodyMedium
            )
            // El código solo es útil mientras la asignación sigue abierta
            if (asignacion.estado != EstadoAsignacion.Finalizada) {
                Text(
                    text = "Código: ${asignacion.codigoTemporal}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Asignada el ${formatearFecha(asignacion.fecha)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}