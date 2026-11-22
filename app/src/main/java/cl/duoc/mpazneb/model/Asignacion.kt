package cl.duoc.mpazneb.model

data class Asignacion(
    val id: Long = 0,
    val actividadId: Long,
    val estudianteId: Long,
    val asistenteId: Long,
    val codigoTemporal: String,
    val fecha: Long = 0,
    val estado: EstadoAsignacion = EstadoAsignacion.Pendiente,
    val intentos: Int = 0,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val observacion: String = ""
)
