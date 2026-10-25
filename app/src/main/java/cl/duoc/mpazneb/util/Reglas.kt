package cl.duoc.mpazneb.util

// 1.º y 2.º siempre; 3.º solo si el administrador lo activó; 4.º nunca
fun permiteLecturaEnVoz(curso: Int, activadaPorAdmin: Boolean): Boolean = when (curso) {
    1, 2 -> true
    3 -> activadaPorAdmin
    else -> false
}