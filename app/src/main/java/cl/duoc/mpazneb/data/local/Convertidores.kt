package cl.duoc.mpazneb.data.local

import androidx.room.TypeConverter
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil

class Convertidores {
    @TypeConverter
    fun perfilAString(perfil: Perfil): String = perfil.clave

    @TypeConverter
    fun stringAPerfil(clave: String): Perfil = Perfil.desde(clave)

    @TypeConverter
    fun moduloAString(modulo: Modulo): String = modulo.clave

    @TypeConverter
    fun stringAModulo(clave: String): Modulo = Modulo.desde(clave)

    @TypeConverter
    fun dificultadAString(dificultad: Dificultad): String = dificultad.clave

    @TypeConverter
    fun stringADificultad(clave: String): Dificultad = Dificultad.desde(clave)

    @TypeConverter
    fun estadoActividadAString(estado: EstadoActividad): String = estado.clave

    @TypeConverter
    fun stringAEstadoActividad(clave: String): EstadoActividad = EstadoActividad.desde(clave)

    @TypeConverter
    fun estadoAsignacionAString(estado: EstadoAsignacion): String = estado.clave

    @TypeConverter
    fun stringAEstadoAsignacion(clave: String): EstadoAsignacion = EstadoAsignacion.desde(clave)
}