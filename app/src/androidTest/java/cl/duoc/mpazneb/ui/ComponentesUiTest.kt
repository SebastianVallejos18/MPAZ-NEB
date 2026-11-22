package cl.duoc.mpazneb.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.mpazneb.model.Pokemon
import cl.duoc.mpazneb.ui.components.MensajeError
import cl.duoc.mpazneb.ui.components.TarjetaPokemon
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Pruebas de UI con Compose (Guía 15, parte 5). Se ejecutan en el teléfono o emulador.
@RunWith(AndroidJUnit4::class)
class ComponentesUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mensajeError_muestraElTextoYElBotonReintentarFunciona() {
        var reintentos = 0
        composeRule.setContent {
            MensajeError(mensaje = "No hay conexión con el servidor", onReintentar = { reintentos++ })
        }

        composeRule.onNodeWithText("No hay conexión con el servidor").assertIsDisplayed()
        composeRule.onNodeWithText("Reintentar").performClick()

        assertEquals(1, reintentos)
    }

    @Test
    fun tarjetaPokemon_muestraNombreYTipoEnEspanol() {
        composeRule.setContent {
            TarjetaPokemon(
                pokemon = Pokemon(id = 25, nombre = "Pikachu", imagenUrl = "", tipos = listOf("electric")),
                cargando = false
            )
        }

        composeRule.onNodeWithText("¡Ganaste un compañero!").assertIsDisplayed()
        composeRule.onNodeWithText("#25 Pikachu").assertIsDisplayed()
        composeRule.onNodeWithText("Tipo: Eléctrico").assertIsDisplayed()
    }

    @Test
    fun tarjetaPokemon_sinPremioNoMuestraNada() {
        composeRule.setContent {
            TarjetaPokemon(pokemon = null, cargando = false)
        }

        composeRule.onNodeWithText("¡Ganaste un compañero!").assertDoesNotExist()
    }
}
