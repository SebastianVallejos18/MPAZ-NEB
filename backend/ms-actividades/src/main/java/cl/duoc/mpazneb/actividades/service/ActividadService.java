package cl.duoc.mpazneb.actividades.service;

import cl.duoc.mpazneb.actividades.exception.RecursoNoEncontradoException;
import cl.duoc.mpazneb.actividades.exception.ReglaNegocioException;
import cl.duoc.mpazneb.actividades.model.Actividad;
import cl.duoc.mpazneb.actividades.model.Dificultad;
import cl.duoc.mpazneb.actividades.model.EstadoActividad;
import cl.duoc.mpazneb.actividades.model.Modulo;
import cl.duoc.mpazneb.actividades.repository.ActividadRepository;
import cl.duoc.mpazneb.actividades.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActividadService {

    private final ActividadRepository actividades;
    private final PreguntaRepository preguntas;

    public ActividadService(ActividadRepository actividades, PreguntaRepository preguntas) {
        this.actividades = actividades;
        this.preguntas = preguntas;
    }

    // Cada filtro es opcional: si llega null, no se aplica
    public List<Actividad> filtrar(Modulo modulo, Integer curso, Dificultad dificultad, EstadoActividad estado) {
        List<Actividad> resultado = new ArrayList<>();
        for (Actividad a : actividades.findAllByOrderByCursoRecomendadoAscNombreAsc()) {
            if (modulo != null && a.getModulo() != modulo) continue;
            if (curso != null && a.getCursoRecomendado() != curso) continue;
            if (dificultad != null && a.getDificultad() != dificultad) continue;
            if (estado != null && a.getEstado() != estado) continue;
            resultado.add(a);
        }
        return resultado;
    }

    public Actividad buscarPorId(Long id) {
        Actividad actividad = actividades.findById(id).orElse(null);
        if (actividad == null) {
            throw new RecursoNoEncontradoException("No existe la actividad " + id);
        }
        return actividad;
    }

    public Actividad crear(Actividad actividad) {
        // Una actividad nueva no tiene preguntas, así que no puede nacer disponible
        if (actividad.getEstado() == EstadoActividad.DISPONIBLE) {
            throw new ReglaNegocioException("Agrega al menos una pregunta antes de dejarla disponible");
        }
        actividad.setId(null);
        normalizar(actividad);
        return actividades.save(actividad);
    }

    public Actividad actualizar(Long id, Actividad datos) {
        Actividad actividad = buscarPorId(id);
        if (datos.getEstado() == EstadoActividad.DISPONIBLE && preguntas.countByActividadId(id) == 0) {
            throw new ReglaNegocioException("Agrega al menos una pregunta antes de dejarla disponible");
        }
        actividad.setNombre(datos.getNombre());
        actividad.setModulo(datos.getModulo());
        actividad.setCursoRecomendado(datos.getCursoRecomendado());
        actividad.setContenido(datos.getContenido());
        actividad.setObjetivoPedagogico(datos.getObjetivoPedagogico());
        actividad.setDificultad(datos.getDificultad());
        actividad.setInstrucciones(datos.getInstrucciones());
        actividad.setTextoBase(datos.getTextoBase());
        actividad.setIntentosPermitidos(datos.getIntentosPermitidos());
        actividad.setLecturaEnVoz(datos.isLecturaEnVoz());
        actividad.setEstado(datos.getEstado());
        normalizar(actividad);
        return actividades.save(actividad);
    }

    // Solo se pueden eliminar borradores, para no dejar asignaciones sin su actividad
    @Transactional
    public void eliminar(Long id) {
        Actividad actividad = buscarPorId(id);
        if (actividad.getEstado() != EstadoActividad.BORRADOR) {
            throw new ReglaNegocioException("Solo se pueden eliminar actividades en borrador");
        }
        actividades.delete(actividad);
    }

    // Mismas reglas que la app: texto solo en Lectura y voz solo en 3.º básico
    private void normalizar(Actividad actividad) {
        if (actividad.getModulo() != Modulo.LECTURA || actividad.getTextoBase() == null) {
            actividad.setTextoBase("");
        }
        if (actividad.getCursoRecomendado() != 3) {
            actividad.setLecturaEnVoz(false);
        }
    }
}
