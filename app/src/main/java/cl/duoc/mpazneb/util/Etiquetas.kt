package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Modulo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun etiquetaModulo(modulo: Modulo): String = when (modulo) {
    Modulo.Lectura -> "Lectura"
    Modulo.Matematica -> "Matemática"
}

fun etiquetaDificultad(dificultad: Dificultad): String = when (dificultad) {
    Dificultad.Facil -> "Fácil"
    Dificultad.Media -> "Media"
    Dificultad.Dificil -> "Difícil"
}

fun etiquetaEstado(estado: EstadoActividad): String = when (estado) {
    EstadoActividad.Borrador -> "Borrador"
    EstadoActividad.Disponible -> "Disponible"
    EstadoActividad.Desactivada -> "Desactivada"
}

fun etiquetaCurso(curso: Int): String = "$curso° básico"

fun tiposDePregunta(modulo: Modulo): List<String> = when (modulo) {
    Modulo.Lectura -> listOf("EXPLICITA", "IMPLICITA", "VOCABULARIO")
    Modulo.Matematica -> listOf("SUMA", "RESTA", "MULTIPLICACION", "PROBLEMA")
}

fun etiquetaTipoPregunta(tipo: String): String = when (tipo) {
    "EXPLICITA" -> "Explícita"
    "IMPLICITA" -> "Implícita"
    "VOCABULARIO" -> "Vocabulario"
    "SUMA" -> "Suma"
    "RESTA" -> "Resta"
    "MULTIPLICACION" -> "Multiplicación"
    "PROBLEMA" -> "Problema"
    else -> tipo
}

// 0 -> "A", 1 -> "B", 2 -> "C"
fun letraOpcion(indice: Int): String = ('A' + indice).toString()

fun etiquetaEstadoAsignacion(estado: EstadoAsignacion): String = when (estado) {
    EstadoAsignacion.Pendiente -> "Pendiente"
    EstadoAsignacion.EnDesarrollo -> "En desarrollo"
    EstadoAsignacion.Finalizada -> "Finalizada"
}

fun formatearFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(millis))