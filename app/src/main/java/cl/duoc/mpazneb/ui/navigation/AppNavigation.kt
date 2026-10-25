package cl.duoc.mpazneb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.mpazneb.ui.screens.InicioProvisorioScreen
import cl.duoc.mpazneb.ui.screens.LoginScreen
import cl.duoc.mpazneb.viewmodel.SesionViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel()
    val usuarioActual by sesionViewModel.usuarioActual.collectAsState()

    val cerrarSesion = {
        sesionViewModel.cerrarSesion()
        navController.navigate(Ruta.Login.ruta) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Ruta.Login.ruta
    ) {
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    sesionViewModel.iniciarSesion(usuario)
                    navController.navigate(rutaInicioDe(usuario.perfil)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.InicioAdmin.ruta) {
            InicioProvisorioScreen("Administración pedagógica", usuarioActual, cerrarSesion)
        }

        composable(Ruta.InicioAsistente.ruta) {
            InicioProvisorioScreen("Asistente de aula", usuarioActual, cerrarSesion)
        }

        composable(Ruta.InicioEstudiante.ruta) {
            InicioProvisorioScreen("Estudiante", usuarioActual, cerrarSesion)
        }
    }
}