package cl.duoc.mpazneb.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class LectorVoz(private val contexto: Context) {

    private var motor: TextToSpeech? = null

    private val _disponible = MutableStateFlow(false)
    val disponible: StateFlow<Boolean> = _disponible.asStateFlow()

    private val _hablando = MutableStateFlow(false)
    val hablando: StateFlow<Boolean> = _hablando.asStateFlow()

    // El motor solo se crea si la actividad realmente usa voz
    fun preparar() {
        if (motor != null) return
        motor = TextToSpeech(contexto) { estado ->
            if (estado == TextToSpeech.SUCCESS) configurar() else _disponible.value = false
        }
    }

    private fun configurar() {
        val tts = motor ?: return

        var resultado = tts.setLanguage(Locale.forLanguageTag("es-CL"))
        if (resultado == TextToSpeech.LANG_MISSING_DATA || resultado == TextToSpeech.LANG_NOT_SUPPORTED) {
            resultado = tts.setLanguage(Locale.forLanguageTag("es"))
        }
        if (resultado == TextToSpeech.LANG_MISSING_DATA || resultado == TextToSpeech.LANG_NOT_SUPPORTED) {
            _disponible.value = false
            return
        }

        tts.setSpeechRate(0.85f) // un poco más lento, pensado para niños que están aprendiendo a leer

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _hablando.value = true
            }

            override fun onDone(utteranceId: String?) {
                _hablando.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _hablando.value = false
            }
        })

        _disponible.value = true
    }

    fun leer(texto: String) {
        if (!_disponible.value || texto.isBlank()) return
        motor?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "lectura")
    }

    fun detener() {
        motor?.stop()
        _hablando.value = false
    }

    // Se libera el motor cuando ya no se usa
    fun liberar() {
        motor?.stop()
        motor?.shutdown()
        motor = null
        _hablando.value = false
        _disponible.value = false
    }
}