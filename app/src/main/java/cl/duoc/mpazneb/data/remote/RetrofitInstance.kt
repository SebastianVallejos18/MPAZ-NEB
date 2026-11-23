package cl.duoc.mpazneb.data.remote

import cl.duoc.mpazneb.data.remote.api.ActividadApi
import cl.duoc.mpazneb.data.remote.api.AsignacionApi
import cl.duoc.mpazneb.data.remote.api.PokeApiService
import cl.duoc.mpazneb.data.remote.api.UsuarioApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton con la configuración de Retrofit (igual que en la Guía 14, pero con una URL por microservicio)
object RetrofitInstance {

    // Dirección del computador donde corren los microservicios:
    // - "127.0.0.1" -> celular físico por USB usando "adb reverse" (ver README)
    // - "10.0.2.2"  -> emulador de Android Studio
    // - "192.168.x.x" -> celular por Wi-Fi: IP del computador en la misma red
    private const val IP_SERVIDOR = "127.0.0.1"

    // Un puerto por microservicio
    const val URL_USUARIOS = "http://$IP_SERVIDOR:8081/"
    const val URL_ACTIVIDADES = "http://$IP_SERVIDOR:8082/"
    const val URL_ASIGNACIONES = "http://$IP_SERVIDOR:8083/"

    // PokeAPI
    const val URL_POKEAPI = "https://pokeapi.co/api/v2/"

    // Muestra en el Logcat cada petición y respuesta (útil para la defensa)
    private val registro = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(registro)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun crearRetrofit(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // "by lazy": cada servicio se crea la primera vez que se usa
    val usuarioApi: UsuarioApi by lazy { crearRetrofit(URL_USUARIOS).create(UsuarioApi::class.java) }
    val actividadApi: ActividadApi by lazy { crearRetrofit(URL_ACTIVIDADES).create(ActividadApi::class.java) }
    val asignacionApi: AsignacionApi by lazy { crearRetrofit(URL_ASIGNACIONES).create(AsignacionApi::class.java) }
    val pokeApi: PokeApiService by lazy { crearRetrofit(URL_POKEAPI).create(PokeApiService::class.java) }
}
