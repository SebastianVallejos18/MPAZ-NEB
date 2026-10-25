package cl.duoc.mpazneb.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import cl.duoc.mpazneb.ui.screens.DetalleAsignacionScreen
import cl.duoc.mpazneb.ui.screens.FormularioActividadScreen
import cl.duoc.mpazneb.ui.screens.FormularioPreguntaScreen
import cl.duoc.mpazneb.ui.screens.IngresoEstudianteScreen
import cl.duoc.mpazneb.ui.screens.LoginScreen
import cl.duoc.mpazneb.ui.screens.PreguntasActividadScreen
import cl.duoc.mpazneb.ui.screens.ResultadosScreen
import cl.duoc.mpazneb.ui.screens.SesionEstudianteScreen
import cl.duoc.mpazneb.viewmodel.SesionViewModel

private const val DURACION_TRANSICION_MS = 1000
private const val FRACCION_DESPLAZAMIENTO = 4
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
        startDestination = Ruta.Login.ruta,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(DURACION_TRANSICION_MS),
                initialOffset = { ancho -> ancho / FRACCION_DESPLAZAMIENTO }
            ) + fadeIn(tween(DURACION_TRANSICION_MS))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(DURACION_TRANSICION_MS),
                targetOffset = { ancho -> ancho / FRACCION_DESPLAZAMIENTO }
            ) + fadeOut(tween(DURACION_TRANSICION_MS))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(DURACION_TRANSICION_MS),
                initialOffset = { ancho -> ancho / FRACCION_DESPLAZAMIENTO }
            ) + fadeIn(tween(DURACION_TRANSICION_MS))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(DURACION_TRANSICION_MS),
                targetOffset = { ancho -> ancho / FRACCION_DESPLAZAMIENTO }
            ) + fadeOut(tween(DURACION_TRANSICION_MS))
        }
    ) {

        // Login --------------------------------------------------------------

        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    sesionViewModel.iniciarSesion(usuario)
                    navController.navigate(rutaInicioDe(usuario.perfil)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onIngresoEstudiante = { navController.navigate(Ruta.IngresoEstudiante.ruta) }
            )
        }

        // Admin --------------------------------------------------------------

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

        // Asistente --------------------------------------------------------------

        composable(Ruta.InicioAsistente.ruta) {
            AsistenteScreen(
                usuario = usuarioActual,
                onCerrarSesion = cerrarSesion,
                onVerResultados = { navController.navigate(Ruta.ResultadosAsistente.ruta) }
            )
        }

        composable(Ruta.ResultadosAsistente.ruta) {
            ResultadosScreen(
                asistenteId = usuarioActual?.id ?: 0L,
                onVerDetalle = { id -> navController.navigate(Ruta.DetalleAsignacion.conId(id)) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Ruta.DetalleAsignacion.ruta,
            arguments = listOf(navArgument("asignacionId") { type = NavType.LongType })
        ) { entrada ->
            val asignacionId = entrada.arguments?.getLong("asignacionId") ?: 0L
            DetalleAsignacionScreen(
                asignacionId = asignacionId,
                onVolver = { navController.popBackStack() }
            )
        }

        // Estudiante --------------------------------------------------------------

        composable(Ruta.IngresoEstudiante.ruta) {
            IngresoEstudianteScreen(
                onIngresoExitoso = { estudiante, asignacionId ->
                    sesionViewModel.iniciarSesion(estudiante)
                    navController.navigate(Ruta.SesionEstudiante.conId(asignacionId)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Ruta.SesionEstudiante.ruta,
            arguments = listOf(navArgument("asignacionId") { type = NavType.LongType })
        ) { entrada ->
            val asignacionId = entrada.arguments?.getLong("asignacionId") ?: 0L
            SesionEstudianteScreen(
                asignacionId = asignacionId,
                onSalir = cerrarSesion
            )
        }
    }
}