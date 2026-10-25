package cl.duoc.mpazneb

import android.app.Application
import androidx.room.Room
import cl.duoc.mpazneb.data.local.AppDatabase
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.data.sembrarDatosIniciales
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MpazApplication : Application() {

    lateinit var baseDeDatos: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        baseDeDatos = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "mpaz_neb.db"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            sembrarDatosIniciales(
                UsuarioRepository(baseDeDatos.usuarioDao),
                ActividadRepository(baseDeDatos.actividadDao, baseDeDatos.preguntaDao)
            )
        }
    }
}