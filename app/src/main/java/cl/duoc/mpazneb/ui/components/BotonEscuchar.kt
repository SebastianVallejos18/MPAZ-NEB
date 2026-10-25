package cl.duoc.mpazneb.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BotonEscuchar(
    hablando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        Icon(
            imageVector = if (hablando) Icons.Filled.Close else Icons.Filled.PlayArrow,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(if (hablando) "Detener" else "Escuchar")
    }
}