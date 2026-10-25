package cl.duoc.mpazneb.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.ui.components.CampoTexto
import cl.duoc.mpazneb.ui.components.SelectorChips
import cl.duoc.mpazneb.util.etiquetaTipoPregunta
import cl.duoc.mpazneb.util.letraOpcion
import cl.duoc.mpazneb.util.tiposDePregunta
import cl.duoc.mpazneb.viewmodel.FormularioPreguntaViewModel

@Composable
fun FormularioPreguntaScreen(
    actividadId: Long,
    preguntaId: Long,
    onVolver: () -> Unit,
    viewModel: FormularioPreguntaViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(actividadId, preguntaId) {
        viewModel.cargar(actividadId, preguntaId)
    }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onVolver()
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
            Text(
                text = if (preguntaId == 0L) "Nueva pregunta" else "Editar pregunta",
                style = MaterialTheme.typography.headlineSmall
            )

            SelectorChips(
                titulo = "Tipo de pregunta",
                opciones = tiposDePregunta(estado.modulo),
                seleccionada = estado.tipo,
                etiqueta = { etiquetaTipoPregunta(it) },
                onSeleccionar = viewModel::onTipoChange
            )

            CampoTexto(
                valor = estado.enunciado,
                onValorChange = viewModel::onEnunciadoChange,
                etiqueta = "Enunciado",
                error = estado.errorEnunciado,
                minLineas = 2
            )

            CampoTexto(
                valor = estado.opcionA,
                onValorChange = viewModel::onOpcionAChange,
                etiqueta = "Opción A",
                error = estado.errorOpcionA
            )

            CampoTexto(
                valor = estado.opcionB,
                onValorChange = viewModel::onOpcionBChange,
                etiqueta = "Opción B",
                error = estado.errorOpcionB
            )

            CampoTexto(
                valor = estado.opcionC,
                onValorChange = viewModel::onOpcionCChange,
                etiqueta = "Opción C",
                error = estado.errorOpcionC
            )

            if (estado.errorOpciones != null) {
                Text(
                    text = estado.errorOpciones ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            SelectorChips(
                titulo = "Respuesta correcta",
                opciones = listOf(0, 1, 2),
                seleccionada = estado.correcta,
                etiqueta = { "Opción ${letraOpcion(it)}" },
                onSeleccionar = viewModel::onCorrectaChange
            )

            AnimatedVisibility(visible = estado.modulo == Modulo.Matematica) {
                CampoTexto(
                    valor = estado.procedimiento,
                    onValorChange = viewModel::onProcedimientoChange,
                    etiqueta = "Procedimiento (ej: 8 + 5 = 13)",
                    error = estado.errorProcedimiento
                )
            }

            CampoTexto(
                valor = estado.retroalimentacion,
                onValorChange = viewModel::onRetroalimentacionChange,
                etiqueta = "Retroalimentación para el estudiante",
                error = estado.errorRetroalimentacion,
                minLineas = 2
            )

            if (estado.errorGeneral != null) {
                Text(
                    text = estado.errorGeneral ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = viewModel::onGuardar,
                enabled = !estado.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar pregunta", style = MaterialTheme.typography.titleMedium)
                }
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}