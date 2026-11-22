package cl.duoc.mpazneb

import android.app.Application

// Ya no se crea la base local (Room): los datos viven en los microservicios.
// Las dependencias se obtienen desde di/AppModule.
class MpazApplication : Application()
