package cl.duoc.mpazneb.model

// Modelo que usa la app. Los datos vienen del microservicio ms-actividades.
data class Actividad(
    val id: Long = 0,
    val nombre: String,
    val modulo: Modulo,
    val cursoRecomendado: Int,
    val contenido: String,
    val objetivoPedagogico: String,
    val dificultad: Dificultad,
    val instrucciones: String,
    val textoBase: String = "",
    val intentosPermitidos: Int = 1,
    val lecturaEnVoz: Boolean = false,
    val estado: EstadoActividad = EstadoActividad.Borrador
)
