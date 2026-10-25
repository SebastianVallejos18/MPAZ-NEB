package cl.duoc.mpazneb.util

// Sin 0/O ni 1/I/L para evitar confusiones al leer o escribir el código
private const val ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"

fun generarCodigo(largo: Int = 6): String =
    (1..largo).map { ALFABETO.random() }.joinToString("")