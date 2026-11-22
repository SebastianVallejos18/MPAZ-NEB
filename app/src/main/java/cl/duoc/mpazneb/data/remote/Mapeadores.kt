package cl.duoc.mpazneb.data.remote

import cl.duoc.mpazneb.data.remote.dto.ActividadDto
import cl.duoc.mpazneb.data.remote.dto.AsignacionDto
import cl.duoc.mpazneb.data.remote.dto.PokemonDto
import cl.duoc.mpazneb.data.remote.dto.PreguntaDto
import cl.duoc.mpazneb.data.remote.dto.ProgresoRequest
import cl.duoc.mpazneb.data.remote.dto.RespuestaDto
import cl.duoc.mpazneb.data.remote.dto.RespuestaRequest
import cl.duoc.mpazneb.data.remote.dto.UsuarioDto
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Pokemon
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.model.Respuesta
import cl.duoc.mpazneb.model.Usuario

// Convierten entre lo que viaja por la red (DTO) y lo que usa la app (modelo).
// Así, si el JSON cambia, solo se ajusta este archivo y no las pantallas.

fun UsuarioDto.aModelo(): Usuario = Usuario(
    id = id,
    alias = alias,
    perfil = Perfil.desde(perfil),
    activo = activo,
    ultimoAcceso = ultimoAcceso ?: 0
)

fun ActividadDto.aModelo(): Actividad = Actividad(
    id = id,
    nombre = nombre,
    modulo = Modulo.desde(modulo),
    cursoRecomendado = cursoRecomendado,
    contenido = contenido,
    objetivoPedagogico = objetivoPedagogico,
    dificultad = Dificultad.desde(dificultad),
    instrucciones = instrucciones,
    textoBase = textoBase ?: "",
    intentosPermitidos = intentosPermitidos,
    lecturaEnVoz = lecturaEnVoz,
    estado = EstadoActividad.desde(estado)
)

fun Actividad.aDto(): ActividadDto = ActividadDto(
    id = id,
    nombre = nombre,
    modulo = modulo.clave,
    cursoRecomendado = cursoRecomendado,
    contenido = contenido,
    objetivoPedagogico = objetivoPedagogico,
    dificultad = dificultad.clave,
    instrucciones = instrucciones,
    textoBase = textoBase,
    intentosPermitidos = intentosPermitidos,
    lecturaEnVoz = lecturaEnVoz,
    estado = estado.clave
)

fun PreguntaDto.aModelo(): Pregunta = Pregunta(
    id = id,
    actividadId = actividadId,
    tipo = tipo,
    enunciado = enunciado,
    opcionA = opcionA,
    opcionB = opcionB,
    opcionC = opcionC,
    respuestaCorrecta = respuestaCorrecta,
    procedimiento = procedimiento ?: "",
    retroalimentacion = retroalimentacion
)

fun Pregunta.aDto(): PreguntaDto = PreguntaDto(
    id = id,
    actividadId = actividadId,
    tipo = tipo,
    enunciado = enunciado,
    opcionA = opcionA,
    opcionB = opcionB,
    opcionC = opcionC,
    respuestaCorrecta = respuestaCorrecta,
    procedimiento = procedimiento,
    retroalimentacion = retroalimentacion
)

fun AsignacionDto.aModelo(): Asignacion = Asignacion(
    id = id,
    actividadId = actividadId,
    estudianteId = estudianteId,
    asistenteId = asistenteId,
    codigoTemporal = codigoTemporal,
    fecha = fecha,
    estado = EstadoAsignacion.desde(estado),
    intentos = intentos,
    aciertos = aciertos,
    errores = errores,
    observacion = observacion ?: ""
)

fun Asignacion.aProgreso(): ProgresoRequest = ProgresoRequest(
    estado = estado.clave,
    intentos = intentos,
    aciertos = aciertos,
    errores = errores
)

fun RespuestaDto.aModelo(): Respuesta = Respuesta(
    id = id,
    asignacionId = asignacionId,
    preguntaId = preguntaId,
    intento = intento,
    opcionElegida = opcionElegida,
    esCorrecta = esCorrecta,
    fecha = fecha
)

fun Respuesta.aRequest(): RespuestaRequest = RespuestaRequest(
    preguntaId = preguntaId,
    intento = intento,
    opcionElegida = opcionElegida,
    esCorrecta = esCorrecta
)

// Prefiere la ilustración oficial (más grande); si no existe, usa el sprite normal
fun PokemonDto.aModelo(): Pokemon = Pokemon(
    id = id,
    nombre = name.replaceFirstChar { it.uppercase() },
    imagenUrl = sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault ?: "",
    tipos = types.sortedBy { it.slot }.map { it.type.name }
)
