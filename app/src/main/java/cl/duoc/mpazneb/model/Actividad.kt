package cl.duoc.mpazneb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class Actividad(
    @PrimaryKey(autoGenerate = true)
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
    val estado: EstadoActividad = EstadoActividad.Borrador
)