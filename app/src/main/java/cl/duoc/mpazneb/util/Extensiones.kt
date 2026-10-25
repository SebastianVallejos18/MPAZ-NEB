package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.model.Pregunta

// Las tres opciones como lista, para recorrerlas con índice (0, 1, 2)

fun Pregunta.opciones(): List<String> = listOf(opcionA, opcionB, opcionC)
// Lo que se lee en voz alta: enunciado y luego cada opción
fun Pregunta.textoParaLeer(): String =
    enunciado + ". " + opciones().mapIndexed { indice, texto ->
        "Opción ${letraOpcion(indice)}: $texto"
    }.joinToString(". ")