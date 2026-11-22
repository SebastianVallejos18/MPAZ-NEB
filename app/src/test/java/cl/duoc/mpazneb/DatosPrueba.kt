package cl.duoc.mpazneb

import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Pokemon
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.model.Usuario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Datos ficticios reutilizables en todas las pruebas

val admin = Usuario(id = 1, alias = "admin", perfil = Perfil.Administrador)
val asistente = Usuario(id = 2, alias = "asistente1", perfil = Perfil.Asistente)
val estudiante = Usuario(id = 3, alias = "estudiante1", perfil = Perfil.Estudiante)

fun actividadDePrueba(
    id: Long = 10,
    estado: EstadoActividad = EstadoActividad.Disponible,
    curso: Int = 1,
    intentos: Int = 2
) = Actividad(
    id = id,
    nombre = "Sumas hasta 20",
    modulo = Modulo.Matematica,
    cursoRecomendado = curso,
    contenido = "Adición",
    objetivoPedagogico = "Resolver sumas simples",
    dificultad = Dificultad.Facil,
    instrucciones = "Resuelve cada suma con calma",
    intentosPermitidos = intentos,
    estado = estado
)

fun preguntaDePrueba(id: Long = 100, actividadId: Long = 10, correcta: Int = 1) = Pregunta(
    id = id,
    actividadId = actividadId,
    tipo = "SUMA",
    enunciado = "¿Cuánto es 8 + 5?",
    opcionA = "12",
    opcionB = "13",
    opcionC = "14",
    respuestaCorrecta = correcta,
    procedimiento = "8 + 5 = 13",
    retroalimentacion = "Cuenta 5 números desde el 8"
)

fun asignacionDePrueba(
    id: Long = 50,
    estado: EstadoAsignacion = EstadoAsignacion.Pendiente,
    intentos: Int = 0,
    aciertos: Int = 0,
    errores: Int = 0
) = Asignacion(
    id = id,
    actividadId = 10,
    estudianteId = 3,
    asistenteId = 2,
    codigoTemporal = "AB3K7P",
    fecha = 1_700_000_000_000,
    estado = estado,
    intentos = intentos,
    aciertos = aciertos,
    errores = errores
)

val pikachu = Pokemon(id = 25, nombre = "Pikachu", imagenUrl = "https://img/25.png", tipos = listOf("electric"))

// Simula una respuesta de error del microservicio, por ejemplo 404 o 409
fun errorHttp(codigo: Int, cuerpo: String = "{\"mensaje\":\"Mensaje del servidor\",\"estado\":$codigo}") =
    HttpException(Response.error<Any>(codigo, cuerpo.toResponseBody("application/json".toMediaType())))
