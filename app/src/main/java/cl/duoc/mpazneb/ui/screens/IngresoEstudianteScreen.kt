package cl.duoc.mpazneb.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.viewmodel.IngresoEstudianteViewModel

@Composable
fun IngresoEstudianteScreen(
    onIngresoExitoso: (Usuario, Long) -> Unit,
    onVolver: () -> Unit,
    viewModel: IngresoEstudianteViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(estado.asignacionId) {
        val id = estado.asignacionId
        val estudiante = estado.estudiante
        if (id != null && estudiante != null) onIngresoExitoso(estudiante, id)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Hola!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Escribe el código que te dio tu asistente",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = estado.codigo,
                onValueChange = viewModel::onCodigoChange,
                label = { Text("Código") },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 6.sp
                ),
                trailingIcon = {
                    if (estado.errorCodigo != null) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Error en el código",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = estado.errorCodigo != null,
                supportingText = { estado.errorCodigo?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = estado.errorGeneral != null) {
                Text(
                    text = estado.errorGeneral ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = viewModel::onIngresar,
                enabled = !estado.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Entrar", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Volver")
            }
        }
    }
}