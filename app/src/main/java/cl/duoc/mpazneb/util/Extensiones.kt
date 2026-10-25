package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.model.Pregunta

// Las tres opciones como lista, para recorrerlas con índice (0, 1, 2)
fun Pregunta.opciones(): List<String> = listOf(opcionA, opcionB, opcionC)