package cl.duoc.mpazneb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.mpazneb.ui.screens.AsistenteScreen
import cl.duoc.mpazneb.ui.screens.CatalogoScreen
import cl.duoc.mpazneb.ui.screens.FormularioActividadScreen
import cl.duoc.mpazneb.ui.screens.FormularioPreguntaScreen
import cl.duoc.mpazneb.ui.screens.InicioProvisorioScreen
import cl.duoc.mpazneb.ui.screens.LoginScreen
import cl.duoc.mpazneb.ui.screens.PreguntasActividadScreen
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
            CatalogoScreen(
                usuario = usuarioActual,
                onCerrarSesion = cerrarSesion,
                onNuevaActividad = { navController.navigate(Ruta.FormularioActividad.conId(0)) },
                onEditarActividad = { id -> navController.navigate(Ruta.FormularioActividad.conId(id)) },
                onPreguntas = { id -> navController.navigate(Ruta.PreguntasActividad.conId(id)) }
            )
        }

        composable(
            route = Ruta.FormularioActividad.ruta,
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { entrada ->
            val actividadId = entrada.arguments?.getLong("actividadId") ?: 0L
            FormularioActividadScreen(
                actividadId = actividadId,
                onVolver = { navController.popBackStack() },
                onActividadCreada = { idNuevo ->
                    navController.navigate(Ruta.PreguntasActividad.conId(idNuevo)) {
                        popUpTo(Ruta.FormularioActividad.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Ruta.PreguntasActividad.ruta,
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { entrada ->
            val actividadId = entrada.arguments?.getLong("actividadId") ?: 0L
            PreguntasActividadScreen(
                actividadId = actividadId,
                onNuevaPregunta = {
                    navController.navigate(Ruta.FormularioPregunta.con(actividadId, 0))
                },
                onEditarPregunta = { preguntaId ->
                    navController.navigate(Ruta.FormularioPregunta.con(actividadId, preguntaId))
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Ruta.FormularioPregunta.ruta,
            arguments = listOf(
                navArgument("actividadId") { type = NavType.LongType },
                navArgument("preguntaId") { type = NavType.LongType }
            )
        ) { entrada ->
            val actividadId = entrada.arguments?.getLong("actividadId") ?: 0L
            val preguntaId = entrada.arguments?.getLong("preguntaId") ?: 0L
            FormularioPreguntaScreen(
                actividadId = actividadId,
                preguntaId = preguntaId,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Ruta.InicioAsistente.ruta) {
            AsistenteScreen(
                usuario = usuarioActual,
                onCerrarSesion = cerrarSesion
            )
        }

        composable(Ruta.InicioEstudiante.ruta) {
            InicioProvisorioScreen("Estudiante", usuarioActual, cerrarSesion)
        }
    }
}