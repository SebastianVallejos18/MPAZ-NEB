package cl.duoc.mpazneb.data

import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.model.Usuario
import cl.duoc.mpazneb.util.hashClave

suspend fun sembrarDatosIniciales(
    usuarioRepo: UsuarioRepository,
    actividadRepo: ActividadRepository
) {
    if (usuarioRepo.contar() == 0) {
        usuarioRepo.registrar(Usuario(alias = "admin", claveHash = hashClave("admin123"), perfil = Perfil.Administrador))
        usuarioRepo.registrar(Usuario(alias = "asistente1", claveHash = hashClave("asist123"), perfil = Perfil.Asistente))
        // Estudiantes ficticios: sin clave, solo entran con código temporal
        usuarioRepo.registrar(Usuario(alias = "estudiante1", claveHash = "", perfil = Perfil.Estudiante))
        usuarioRepo.registrar(Usuario(alias = "estudiante2", claveHash = "", perfil = Perfil.Estudiante))
    }

    if (actividadRepo.contar() == 0) {
        actividadRepo.crear(
            Actividad(
                nombre = "Misifú y la lluvia",
                modulo = Modulo.Lectura,
                cursoRecomendado = 1,
                contenido = "Comprensión lectora de textos breves",
                objetivoPedagogico = "Localizar información explícita e inferir causas sencillas",
                dificultad = Dificultad.Facil,
                instrucciones = "Lee el texto con calma y luego responde.",
                textoBase = "Misifú es un gato gris. Hoy llueve mucho. Misifú mira la lluvia desde la ventana. " +
                        "Su dueña le da leche tibia. Misifú se duerme en su cama.",
                intentosPermitidos = 2,
                estado = EstadoActividad.Disponible
            ),
            listOf(
                Pregunta(
                    actividadId = 0, tipo = "EXPLICITA",
                    enunciado = "¿De qué color es Misifú?",
                    opcionA = "Gris", opcionB = "Negro", opcionC = "Blanco",
                    respuestaCorrecta = 0,
                    retroalimentacion = "El texto dice que Misifú es un gato gris. ¡Buen trabajo si lo encontraste!"
                ),
                Pregunta(
                    actividadId = 0, tipo = "IMPLICITA",
                    enunciado = "¿Por qué crees que Misifú no sale a jugar?",
                    opcionA = "Porque está lloviendo", opcionB = "Porque no tiene cama", opcionC = "Porque no le gusta la leche",
                    respuestaCorrecta = 0,
                    retroalimentacion = "El texto cuenta que llueve mucho, y eso explica por qué se queda adentro."
                ),
                Pregunta(
                    actividadId = 0, tipo = "VOCABULARIO",
                    enunciado = "En el texto, ¿qué significa la palabra tibia?",
                    opcionA = "Muy fría", opcionB = "Ni fría ni muy caliente", opcionC = "Quema la lengua",
                    respuestaCorrecta = 1,
                    retroalimentacion = "Tibia es algo con una temperatura agradable, ni fría ni muy caliente."
                )
            )
        )

        actividadRepo.crear(
            Actividad(
                nombre = "El huerto de Camila",
                modulo = Modulo.Lectura,
                cursoRecomendado = 2,
                contenido = "Comprensión lectora de textos narrativos",
                objetivoPedagogico = "Localizar información, inferir causas y reconocer vocabulario en contexto",
                dificultad = Dificultad.Media,
                instrucciones = "Lee el texto y elige la mejor respuesta.",
                textoBase = "Camila tiene un huerto pequeño detrás de su casa. Cada mañana riega las lechugas y " +
                        "los tomates con una regadera azul. Al principio las plantas eran muy chicas, pero con el " +
                        "cuidado de Camila crecieron fuertes. Ayer cosechó tres lechugas y se las regaló a su vecina.",
                intentosPermitidos = 2,
                estado = EstadoActividad.Disponible
            ),
            listOf(
                Pregunta(
                    actividadId = 0, tipo = "EXPLICITA",
                    enunciado = "¿Con qué riega Camila sus plantas?",
                    opcionA = "Con una manguera", opcionB = "Con una regadera azul", opcionC = "Con un balde rojo",
                    respuestaCorrecta = 1,
                    retroalimentacion = "Camila usa una regadera azul. Puedes volver a leer el texto para comprobarlo."
                ),
                Pregunta(
                    actividadId = 0, tipo = "IMPLICITA",
                    enunciado = "¿Por qué las plantas crecieron fuertes?",
                    opcionA = "Porque Camila las cuidó", opcionB = "Porque llovió todos los días", opcionC = "Porque eran de otro país",
                    respuestaCorrecta = 0,
                    retroalimentacion = "El texto dice que crecieron con el cuidado de Camila."
                ),
                Pregunta(
                    actividadId = 0, tipo = "VOCABULARIO",
                    enunciado = "En el texto, ¿qué significa cosechó?",
                    opcionA = "Recogió lo que había crecido", opcionB = "Plantó semillas", opcionC = "Botó las plantas",
                    respuestaCorrecta = 0,
                    retroalimentacion = "Cosechar es recoger los frutos o verduras cuando ya están listos."
                )
            )
        )

        actividadRepo.crear(
            Actividad(
                nombre = "Sumas hasta 20",
                modulo = Modulo.Matematica,
                cursoRecomendado = 1,
                contenido = "Adición",
                objetivoPedagogico = "Resolver sumas y problemas simples con números hasta 20",
                dificultad = Dificultad.Facil,
                instrucciones = "Resuelve cada suma. Puedes usar tus dedos para contar.",
                intentosPermitidos = 2,
                estado = EstadoActividad.Disponible
            ),
            listOf(
                Pregunta(
                    actividadId = 0, tipo = "SUMA",
                    enunciado = "Tengo 8 manzanas y mi amigo me regala 5 más. ¿Cuántas manzanas tengo ahora?",
                    opcionA = "12", opcionB = "13", opcionC = "14",
                    respuestaCorrecta = 1,
                    procedimiento = "8 + 5 = 13",
                    retroalimentacion = "Puedes partir en el 8 y contar 5 números más: 9, 10, 11, 12, 13."
                ),
                Pregunta(
                    actividadId = 0, tipo = "SUMA",
                    enunciado = "¿Cuánto es 9 + 7?",
                    opcionA = "16", opcionB = "15", opcionC = "17",
                    respuestaCorrecta = 0,
                    procedimiento = "9 + 7 = 16",
                    retroalimentacion = "Una ayuda: 9 + 1 = 10, y todavía faltan 6 más, así que llegas a 16."
                ),
                Pregunta(
                    actividadId = 0, tipo = "SUMA",
                    enunciado = "Tenía 6 globos y compré 9 más. ¿Cuántos globos tengo?",
                    opcionA = "14", opcionB = "16", opcionC = "15",
                    respuestaCorrecta = 2,
                    procedimiento = "6 + 9 = 15",
                    retroalimentacion = "Si juntas todos los globos, 6 + 9 da 15."
                )
            )
        )

        actividadRepo.crear(
            Actividad(
                nombre = "Restas hasta 50",
                modulo = Modulo.Matematica,
                cursoRecomendado = 2,
                contenido = "Sustracción",
                objetivoPedagogico = "Resolver restas y problemas con números hasta 50",
                dificultad = Dificultad.Media,
                instrucciones = "Resuelve cada resta con calma y revisa tu respuesta.",
                intentosPermitidos = 2,
                estado = EstadoActividad.Disponible
            ),
            listOf(
                Pregunta(
                    actividadId = 0, tipo = "RESTA",
                    enunciado = "En una caja hay 45 lápices y se usan 18. ¿Cuántos lápices quedan?",
                    opcionA = "27", opcionB = "63", opcionC = "37",
                    respuestaCorrecta = 0,
                    procedimiento = "45 - 18 = 27",
                    retroalimentacion = "Cuando se usan lápices, la cantidad baja: hay que restar, no sumar."
                ),
                Pregunta(
                    actividadId = 0, tipo = "RESTA",
                    enunciado = "¿Cuánto es 50 - 23?",
                    opcionA = "33", opcionB = "27", opcionC = "28",
                    respuestaCorrecta = 1,
                    procedimiento = "50 - 23 = 27",
                    retroalimentacion = "Puedes restar primero 20 (queda 30) y luego 3 más (queda 27)."
                ),
                Pregunta(
                    actividadId = 0, tipo = "RESTA",
                    enunciado = "Una profesora tiene 36 stickers y reparte 19. ¿Cuántos le quedan?",
                    opcionA = "15", opcionB = "17", opcionC = "55",
                    respuestaCorrecta = 1,
                    procedimiento = "36 - 19 = 17",
                    retroalimentacion = "Repartir significa que se queda con menos. Resta 19 a 36."
                )
            )
        )
    }
}