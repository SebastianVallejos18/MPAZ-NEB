package cl.duoc.mpazneb.model

// La contraseña ya no viaja a la app: el login se valida en el microservicio ms-usuarios
data class Usuario(
    val id: Long = 0,
    val alias: String,
    val perfil: Perfil,
    val activo: Boolean = true,
    val ultimoAcceso: Long = 0
)
