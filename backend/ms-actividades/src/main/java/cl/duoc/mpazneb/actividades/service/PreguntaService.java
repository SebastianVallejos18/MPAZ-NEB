package cl.duoc.mpazneb.actividades.service;

import cl.duoc.mpazneb.actividades.exception.RecursoNoEncontradoException;
import cl.duoc.mpazneb.actividades.exception.ReglaNegocioException;
import cl.duoc.mpazneb.actividades.model.Actividad;
import cl.duoc.mpazneb.actividades.model.EstadoActividad;
import cl.duoc.mpazneb.actividades.model.Modulo;
import cl.duoc.mpazneb.actividades.model.Pregunta;
import cl.duoc.mpazneb.actividades.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntas;
    private final ActividadService actividadService;

    public PreguntaService(PreguntaRepository preguntas, ActividadService actividadService) {
        this.preguntas = preguntas;
        this.actividadService = actividadService;
    }

    public List<Pregunta> deActividad(Long actividadId) {
        actividadService.buscarPorId(actividadId); // 404 si la actividad no existe
        return preguntas.findByActividadIdOrderByIdAsc(actividadId);
    }

    public Pregunta buscarPorId(Long id) {
        Pregunta pregunta = preguntas.findById(id).orElse(null);
        if (pregunta == null) {
            throw new RecursoNoEncontradoException("No existe la pregunta " + id);
        }
        return pregunta;
    }

    public Pregunta crear(Long actividadId, Pregunta pregunta) {
        Actividad actividad = actividadService.buscarPorId(actividadId);
        validarOpcionesDistintas(pregunta);
        pregunta.setId(null);
        pregunta.setActividad(actividad);
        normalizar(pregunta, actividad);
        return preguntas.save(pregunta);
    }

    public Pregunta actualizar(Long id, Pregunta datos) {
        Pregunta pregunta = buscarPorId(id);
        validarOpcionesDistintas(datos);
        pregunta.setTipo(datos.getTipo());
        pregunta.setEnunciado(datos.getEnunciado());
        pregunta.setOpcionA(datos.getOpcionA());
        pregunta.setOpcionB(datos.getOpcionB());
        pregunta.setOpcionC(datos.getOpcionC());
        pregunta.setRespuestaCorrecta(datos.getRespuestaCorrecta());
        pregunta.setProcedimiento(datos.getProcedimiento());
        pregunta.setRetroalimentacion(datos.getRetroalimentacion());
        normalizar(pregunta, pregunta.getActividad());
        return preguntas.save(pregunta);
    }

    // Regla de negocio: una actividad disponible no puede quedar sin preguntas
    @Transactional
    public void eliminar(Long id) {
        Pregunta pregunta = buscarPorId(id);
        Actividad actividad = pregunta.getActividad();
        boolean esLaUnica = preguntas.countByActividadId(actividad.getId()) == 1;
        if (esLaUnica && actividad.getEstado() == EstadoActividad.DISPONIBLE) {
            throw new ReglaNegocioException(
                    "No puedes eliminar la única pregunta de una actividad disponible. Cámbiala primero a borrador.");
        }
        preguntas.delete(pregunta);
    }

    private void validarOpcionesDistintas(Pregunta p) {
        String a = p.getOpcionA().trim().toLowerCase();
        String b = p.getOpcionB().trim().toLowerCase();
        String c = p.getOpcionC().trim().toLowerCase();
        if (a.equals(b) || a.equals(c) || b.equals(c)) {
            throw new ReglaNegocioException("Las tres opciones deben ser distintas");
        }
    }

    // El procedimiento solo se guarda en Matemática
    private void normalizar(Pregunta pregunta, Actividad actividad) {
        if (actividad.getModulo() != Modulo.MATEMATICA || pregunta.getProcedimiento() == null) {
            pregunta.setProcedimiento("");
        }
    }
}
