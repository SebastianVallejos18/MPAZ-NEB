package cl.duoc.mpazneb.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preguntas",
    foreignKeys = [
        ForeignKey(
            entity = Actividad::class,
            parentColumns = ["id"],
            childColumns = ["actividadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("actividadId")]
)
data class Pregunta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actividadId: Long,
    val tipo: String,
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val respuestaCorrecta: Int,
    val procedimiento: String = "",
    val retroalimentacion: String
)