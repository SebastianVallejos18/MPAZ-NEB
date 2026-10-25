package cl.duoc.mpazneb.util

import java.security.MessageDigest

fun hashClave(clave: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(clave.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}