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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.ui.components.CampoTexto
import cl.duoc.mpazneb.ui.components.SelectorChips
import cl.duoc.mpazneb.util.etiquetaCurso
import cl.duoc.mpazneb.util.etiquetaDificultad
import cl.duoc.mpazneb.util.etiquetaEstado
import cl.duoc.mpazneb.util.etiquetaModulo
import cl.duoc.mpazneb.viewmodel.FormularioActividadViewModel

@Composable
fun FormularioActividadScreen(
    actividadId: Long,
    onVolver: () -> Unit,
    onActividadCreada: (Long) -> Unit,
    viewModel: FormularioActividadViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(actividadId) {
        viewModel.cargar(actividadId)
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
                text = if (actividadId == 0L) "Nueva actividad" else "Editar actividad",
                style = MaterialTheme.typography.headlineSmall
            )

            CampoTexto(
                valor = estado.nombre,
                onValorChange = viewModel::onNombreChange,
                etiqueta = "Nombre",
                error = estado.errorNombre
            )

            SelectorChips(
                titulo = "Módulo",
                opciones = listOf(Modulo.Lectura, Modulo.Matematica),
                seleccionada = estado.modulo,
                etiqueta = { etiquetaModulo(it) },
                onSeleccionar = viewModel::onModuloChange
            )

            SelectorChips(
                titulo = "Curso recomendado",
                opciones = listOf(1, 2, 3, 4),
                seleccionada = estado.curso,
                etiqueta = { etiquetaCurso(it) },
                onSeleccionar = viewModel::onCursoChange
            )

            SelectorChips(
                titulo = "Dificultad",
                opciones = listOf(Dificultad.Facil, Dificultad.Media, Dificultad.Dificil),
                seleccionada = estado.dificultad,
                etiqueta = { etiquetaDificultad(it) },
                onSeleccionar = viewModel::onDificultadChange
            )

            CampoTexto(
                valor = estado.contenido,
                onValorChange = viewModel::onContenidoChange,
                etiqueta = "Contenido",
                error = estado.errorContenido
            )

            CampoTexto(
                valor = estado.objetivo,
                onValorChange = viewModel::onObjetivoChange,
                etiqueta = "Objetivo pedagógico",
                error = estado.errorObjetivo,
                minLineas = 2
            )

            CampoTexto(
                valor = estado.instrucciones,
                onValorChange = viewModel::onInstruccionesChange,
                etiqueta = "Instrucciones para el estudiante",
                error = estado.errorInstrucciones,
                minLineas = 2
            )

            // El texto de lectura solo aparece cuando el módulo es Lectura
            AnimatedVisibility(visible = estado.modulo == Modulo.Lectura) {
                CampoTexto(
                    valor = estado.textoBase,
                    onValorChange = viewModel::onTextoBaseChange,
                    etiqueta = "Texto de lectura",
                    error = estado.errorTextoBase,
                    minLineas = 4
                )
            }

            CampoTexto(
                valor = estado.intentos,
                onValorChange = viewModel::onIntentosChange,
                etiqueta = "Intentos permitidos",
                error = estado.errorIntentos,
                tipoTeclado = KeyboardType.Number
            )

            SelectorChips(
                titulo = "Estado",
                opciones = listOf(
                    EstadoActividad.Borrador,
                    EstadoActividad.Disponible,
                    EstadoActividad.Desactivada
                ),
                seleccionada = estado.estado,
                etiqueta = { etiquetaEstado(it) },
                onSeleccionar = viewModel::onEstadoChange,
                error = estado.errorEstado
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
                    Text("Guardar actividad", style = MaterialTheme.typography.titleMedium)
                }
            }

            LaunchedEffect(estado.idCreado) {
                estado.idCreado?.let { onActividadCreada(it) }
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