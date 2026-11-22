package cl.duoc.mpazneb.model

data class Respuesta(
    val id: Long = 0,
    val asignacionId: Long,
    val preguntaId: Long,
    val intento: Int,
    val opcionElegida: Int,
    val esCorrecta: Boolean,
    val fecha: Long = 0
)
