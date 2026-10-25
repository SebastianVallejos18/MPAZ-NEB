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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.ui.components.FilaFiltro
import cl.duoc.mpazneb.util.etiquetaCurso
import cl.duoc.mpazneb.util.etiquetaDificultad
import cl.duoc.mpazneb.util.etiquetaEstado
import cl.duoc.mpazneb.util.etiquetaModulo
import cl.duoc.mpazneb.viewmodel.CatalogoViewModel

@Composable
fun CatalogoScreen(
    usuario: Usuario?,
    onCerrarSesion: () -> Unit,
    onNuevaActividad: () -> Unit,
    onEditarActividad: (Long) -> Unit,
    onPreguntas: (Long) -> Unit,
    viewModel: CatalogoViewModel = viewModel()
) {
    val actividades by viewModel.actividades.collectAsState()
    val filtros by viewModel.filtros.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaActividad,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva actividad") }
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
                        Text(
                            text = "Catálogo de actividades",
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
                    FilaFiltro(
                        titulo = "Estado",
                        opciones = listOf(
                            EstadoActividad.Borrador,
                            EstadoActividad.Disponible,
                            EstadoActividad.Desactivada
                        ),
                        seleccionada = filtros.estado,
                        etiqueta = { etiquetaEstado(it) },
                        onSeleccionar = viewModel::filtrarPorEstado
                    )
                }
            }

            if (actividades.isEmpty()) {
                item {
                    Text(
                        text = "No hay actividades que coincidan con los filtros elegidos.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(actividades, key = { it.id }) { actividad ->
                TarjetaActividad(
                    actividad = actividad,
                    onClick = { onEditarActividad(actividad.id) },
                    onPreguntas = { onPreguntas(actividad.id) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaActividad(
    actividad: Actividad,
    onClick: () -> Unit,
    onPreguntas: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
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
            Text(
                text = etiquetaEstado(actividad.estado),
                style = MaterialTheme.typography.labelLarge,
                color = if (actividad.estado == EstadoActividad.Disponible) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            TextButton(onClick = onPreguntas) {
                Text("Ver preguntas")
            }
        }
    }
}