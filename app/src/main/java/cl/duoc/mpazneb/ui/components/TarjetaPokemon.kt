package cl.duoc.mpazneb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mpazneb.model.Pokemon
import cl.duoc.mpazneb.util.etiquetaTipoPokemon
import coil.compose.AsyncImage

// Premio del estudiante: un Pokémon obtenido desde la PokeAPI
@Composable
fun TarjetaPokemon(
    pokemon: Pokemon?,
    cargando: Boolean,
    modifier: Modifier = Modifier
) {
    if (pokemon == null && !cargando) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "¡Ganaste un compañero!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            if (cargando || pokemon == null) {
                CircularProgressIndicator()
            } else {
                // Coil descarga la imagen desde la URL que entrega la PokeAPI
                AsyncImage(
                    model = pokemon.imagenUrl,
                    contentDescription = "Imagen de ${pokemon.nombre}",
                    modifier = Modifier.size(160.dp)
                )
                Text(
                    text = "#${pokemon.id} ${pokemon.nombre}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Tipo: " + pokemon.tipos.joinToString(" / ") { etiquetaTipoPokemon(it) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
