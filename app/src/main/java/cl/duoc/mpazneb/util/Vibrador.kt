package cl.duoc.mpazneb.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

// ================== AJUSTES DE VIBRACIÓN ==================
// Aquí se personaliza todo. Tiempos en milisegundos; intensidad de 1 (suave) a 255 (fuerte).

private const val VIBRACION_ACTIVA = true

// Acierto: un toque firme
private const val ACIERTO_DURACION_MS = 80L
private const val ACIERTO_INTENSIDAD = 200

// Por reforzar: dos toques suaves
private const val REFUERZO_DURACION_MS = 40L
private const val REFUERZO_PAUSA_MS = 80L
private const val REFUERZO_INTENSIDAD = 100

// Tope de seguridad: ninguna vibración puede durar más que esto
private const val DURACION_MAXIMA_MS = 500L
// ===========================================================

class Vibrador(private val contexto: Context) {

    @Suppress("DEPRECATION")
    private val vibrador: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val administrador = contexto.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            administrador?.defaultVibrator
        } else {
            contexto.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Falso si el ajuste general está apagado o el dispositivo no tiene vibrador (por ejemplo, un emulador)
    private fun disponible(): Boolean = VIBRACION_ACTIVA && vibrador?.hasVibrator() == true

    fun acierto() {
        vibrar(
            duraciones = longArrayOf(0, limitarDuracion(ACIERTO_DURACION_MS)),
            intensidades = intArrayOf(0, limitarIntensidad(ACIERTO_INTENSIDAD))
        )
    }

    fun refuerzo() {
        vibrar(
            duraciones = longArrayOf(
                0,
                limitarDuracion(REFUERZO_DURACION_MS),
                limitarDuracion(REFUERZO_PAUSA_MS),
                limitarDuracion(REFUERZO_DURACION_MS)
            ),
            intensidades = intArrayOf(
                0,
                limitarIntensidad(REFUERZO_INTENSIDAD),
                0,
                limitarIntensidad(REFUERZO_INTENSIDAD)
            )
        )
    }

    fun cancelar() {
        vibrador?.cancel()
    }

    private fun vibrar(duraciones: LongArray, intensidades: IntArray) {
        val servicio = vibrador ?: return
        if (!disponible()) return
        try {
            servicio.vibrate(VibrationEffect.createWaveform(duraciones, intensidades, -1))
        } catch (e: Exception) {
            // Si el sistema rechaza la vibración, la app sigue funcionando sin ella
        }
    }

    private fun limitarDuracion(ms: Long): Long = ms.coerceIn(10L, DURACION_MAXIMA_MS)

    private fun limitarIntensidad(valor: Int): Int = valor.coerceIn(1, 255)
}