package cl.duoc.mpazneb.actividades.config;

import cl.duoc.mpazneb.actividades.model.Actividad;
import cl.duoc.mpazneb.actividades.model.Dificultad;
import cl.duoc.mpazneb.actividades.model.EstadoActividad;
import cl.duoc.mpazneb.actividades.model.Modulo;
import cl.duoc.mpazneb.actividades.model.Pregunta;
import cl.duoc.mpazneb.actividades.repository.ActividadRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Carga 4 actividades ficticias de ejemplo (2 de Lectura y 2 de Matemática) si la tabla está vacía
@Component
public class DatosIniciales implements CommandLineRunner {

    private final ActividadRepository repositorio;

    public DatosIniciales(ActividadRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }

        Actividad misifu = actividad("Misifú y la lluvia", Modulo.LECTURA, 1,
                "Comprensión lectora de textos breves",
                "Localizar información explícita e inferir causas sencillas",
                Dificultad.FACIL, "Lee el texto con calma y luego responde.",
                "Misifú es un gato gris. Hoy llueve mucho. Misifú mira la lluvia desde la ventana. "
                        + "Su dueña le da leche tibia. Misifú se duerme en su cama.");
        agregar(misifu, "EXPLICITA", "¿De qué color es Misifú?", "Gris", "Negro", "Blanco", 0, "",
                "El texto dice que Misifú es un gato gris. ¡Buen trabajo si lo encontraste!");
        agregar(misifu, "IMPLICITA", "¿Por qué crees que Misifú no sale a jugar?",
                "Porque está lloviendo", "Porque no tiene cama", "Porque no le gusta la leche", 0, "",
                "El texto cuenta que llueve mucho, y eso explica por qué se queda adentro.");
        agregar(misifu, "VOCABULARIO", "En el texto, ¿qué significa la palabra tibia?",
                "Muy fría", "Ni fría ni muy caliente", "Quema la lengua", 1, "",
                "Tibia es algo con una temperatura agradable, ni fría ni muy caliente.");
        repositorio.save(misifu);

        Actividad huerto = actividad("El huerto de Camila", Modulo.LECTURA, 2,
                "Comprensión lectora de textos narrativos",
                "Localizar información, inferir causas y reconocer vocabulario en contexto",
                Dificultad.MEDIA, "Lee el texto y elige la mejor respuesta.",
                "Camila tiene un huerto pequeño detrás de su casa. Cada mañana riega las lechugas y "
                        + "los tomates con una regadera azul. Al principio las plantas eran muy chicas, pero con el "
                        + "cuidado de Camila crecieron fuertes. Ayer cosechó tres lechugas y se las regaló a su vecina.");
        agregar(huerto, "EXPLICITA", "¿Con qué riega Camila sus plantas?",
                "Con una manguera", "Con una regadera azul", "Con un balde rojo", 1, "",
                "Camila usa una regadera azul. Puedes volver a leer el texto para comprobarlo.");
        agregar(huerto, "IMPLICITA", "¿Por qué las plantas crecieron fuertes?",
                "Porque Camila las cuidó", "Porque llovió todos los días", "Porque eran de otro país", 0, "",
                "El texto dice que crecieron con el cuidado de Camila.");
        agregar(huerto, "VOCABULARIO", "En el texto, ¿qué significa cosechó?",
                "Recogió lo que había crecido", "Plantó semillas", "Botó las plantas", 0, "",
                "Cosechar es recoger los frutos o verduras cuando ya están listos.");
        repositorio.save(huerto);

        Actividad sumas = actividad("Sumas hasta 20", Modulo.MATEMATICA, 1, "Adición",
                "Resolver sumas y problemas simples con números hasta 20",
                Dificultad.FACIL, "Resuelve cada suma. Puedes usar tus dedos para contar.", "");
        agregar(sumas, "SUMA", "Tengo 8 manzanas y mi amigo me regala 5 más. ¿Cuántas manzanas tengo ahora?",
                "12", "13", "14", 1, "8 + 5 = 13",
                "Puedes partir en el 8 y contar 5 números más: 9, 10, 11, 12, 13.");
        agregar(sumas, "SUMA", "¿Cuánto es 9 + 7?", "16", "15", "17", 0, "9 + 7 = 16",
                "Una ayuda: 9 + 1 = 10, y todavía faltan 6 más, así que llegas a 16.");
        agregar(sumas, "SUMA", "Tenía 6 globos y compré 9 más. ¿Cuántos globos tengo?",
                "14", "16", "15", 2, "6 + 9 = 15", "Si juntas todos los globos, 6 + 9 da 15.");
        repositorio.save(sumas);

        Actividad restas = actividad("Restas hasta 50", Modulo.MATEMATICA, 2, "Sustracción",
                "Resolver restas y problemas con números hasta 50",
                Dificultad.MEDIA, "Resuelve cada resta con calma y revisa tu respuesta.", "");
        agregar(restas, "RESTA", "En una caja hay 45 lápices y se usan 18. ¿Cuántos lápices quedan?",
                "27", "63", "37", 0, "45 - 18 = 27",
                "Cuando se usan lápices, la cantidad baja: hay que restar, no sumar.");
        agregar(restas, "RESTA", "¿Cuánto es 50 - 23?", "33", "27", "28", 1, "50 - 23 = 27",
                "Puedes restar primero 20 (queda 30) y luego 3 más (queda 27).");
        agregar(restas, "RESTA", "Una profesora tiene 36 stickers y reparte 19. ¿Cuántos le quedan?",
                "15", "17", "55", 1, "36 - 19 = 17",
                "Repartir significa que se queda con menos. Resta 19 a 36.");
        repositorio.save(restas);
    }

    private Actividad actividad(String nombre, Modulo modulo, int curso, String contenido, String objetivo,
                                Dificultad dificultad, String instrucciones, String texto) {
        Actividad a = new Actividad();
        a.setNombre(nombre);
        a.setModulo(modulo);
        a.setCursoRecomendado(curso);
        a.setContenido(contenido);
        a.setObjetivoPedagogico(objetivo);
        a.setDificultad(dificultad);
        a.setInstrucciones(instrucciones);
        a.setTextoBase(texto);
        a.setIntentosPermitidos(2);
        a.setEstado(EstadoActividad.DISPONIBLE);
        return a;
    }

    // Gracias a cascade = ALL, al guardar la actividad también se guardan sus preguntas
    private void agregar(Actividad actividad, String tipo, String enunciado, String a, String b, String c,
                         int correcta, String procedimiento, String retroalimentacion) {
        Pregunta p = new Pregunta();
        p.setActividad(actividad);
        p.setTipo(tipo);
        p.setEnunciado(enunciado);
        p.setOpcionA(a);
        p.setOpcionB(b);
        p.setOpcionC(c);
        p.setRespuestaCorrecta(correcta);
        p.setProcedimiento(procedimiento);
        p.setRetroalimentacion(retroalimentacion);
        actividad.getPreguntas().add(p);
    }
}
