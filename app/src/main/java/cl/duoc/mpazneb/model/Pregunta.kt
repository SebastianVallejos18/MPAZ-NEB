package cl.duoc.mpazneb.model

data class Pregunta(
    val id: Long = 0,
    val actividadId: Long,
    val tipo: String,
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val respuestaCorrecta: Int,
    val procedimiento: String = "",
    val retroalimentacion: String
)
