package cl.duoc.mpazneb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.ui.components.FilaFiltro
import cl.duoc.mpazneb.util.etiquetaCurso
import cl.duoc.mpazneb.util.etiquetaDificultad
import cl.duoc.mpazneb.util.etiquetaModulo
import cl.duoc.mpazneb.viewmodel.AsistenteViewModel

@Composable
fun AsistenteScreen(
    usuario: Usuario?,
    onCerrarSesion: () -> Unit,
    viewModel: AsistenteViewModel = viewModel()
) {
    val actividades by viewModel.actividades.collectAsState()
    val estudiantes by viewModel.estudiantes.collectAsState()
    val filtros by viewModel.filtros.collectAsState()
    val estado by viewModel.uiState.collectAsState()

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
                        Text(
                            text = "Asignar actividades",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Sesión de ${usuario?.alias ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir")
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilaFiltro(
                        titulo = "Módulo",
                        opciones = listOf(Modulo.Lectura, Modulo.Matematica),
                        seleccionada = filtros.modulo,
                        etiqueta = { etiquetaModulo(it) },
                        onSeleccionar = viewModel::filtrarPorModulo
                    )
                    FilaFiltro(
                        titulo = "Curso",
                        opciones = listOf(1, 2, 3, 4),
                        seleccionada = filtros.curso,
                        etiqueta = { etiquetaCurso(it) },
                        onSeleccionar = viewModel::filtrarPorCurso
                    )
                    FilaFiltro(
                        titulo = "Dificultad",
                        opciones = listOf(Dificultad.Facil, Dificultad.Media, Dificultad.Dificil),
                        seleccionada = filtros.dificultad,
                        etiqueta = { etiquetaDificultad(it) },
                        onSeleccionar = viewModel::filtrarPorDificultad
                    )
                }
            }

            if (actividades.isEmpty()) {
                item {
                    Text(
                        text = "No hay actividades disponibles con esos filtros.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(actividades, key = { it.id }) { actividad ->
                TarjetaAsignable(
                    actividad = actividad,
                    onAsignar = { viewModel.pedirAsignar(actividad) }
                )
            }
        }
    }

    estado.actividadPorAsignar?.let { actividad ->
        DialogoElegirEstudiante(
            actividad = actividad,
            estudiantes = estudiantes,
            elegido = estado.estudianteElegido,
            error = estado.errorAsignacion,
            cargando = estado.cargando,
            onElegir = viewModel::elegirEstudiante,
            onConfirmar = { usuario?.let { viewModel.confirmarAsignacion(it.id) } },
            onCancelar = viewModel::cancelarAsignacion
        )
    }

    estado.codigoGenerado?.let { codigo ->
        DialogoCodigo(
            codigo = codigo,
            alias = estado.aliasAsignado ?: "",
            onCerrar = viewModel::cerrarCodigo
        )
    }
}

@Composable
private fun TarjetaAsignable(actividad: Actividad, onAsignar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = actividad.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${etiquetaModulo(actividad.modulo)} · " +
                        "${etiquetaCurso(actividad.cursoRecomendado)} · " +
                        etiquetaDificultad(actividad.dificultad),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = actividad.objetivoPedagogico, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onAsignar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Asignar a un estudiante")
            }
        }
    }
}

@Composable
private fun DialogoElegirEstudiante(
    actividad: Actividad,
    estudiantes: List<Usuario>,
    elegido: Usuario?,
    error: String?,
    cargando: Boolean,
    onElegir: (Usuario) -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Asignar actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = actividad.nombre, style = MaterialTheme.typography.titleSmall)
                Text(text = "Elige el estudiante:", style = MaterialTheme.typography.bodyMedium)

                if (estudiantes.isEmpty()) {
                    Text(
                        text = "No hay estudiantes registrados.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                estudiantes.forEach { estudiante ->
                    val seleccionado = estudiante.id == elegido?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = seleccionado,
                                onClick = { onElegir(estudiante) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = seleccionado, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(text = estudiante.alias, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmar, enabled = !cargando) {
                Text("Asignar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DialogoCodigo(codigo: String, alias: String, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Actividad asignada") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Entrega este código a $alias:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = codigo,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Sirve hasta que el estudiante termine la actividad.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Listo")
            }
        }
    )
}