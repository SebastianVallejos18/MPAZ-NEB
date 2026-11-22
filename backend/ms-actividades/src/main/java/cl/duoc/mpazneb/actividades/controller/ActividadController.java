package cl.duoc.mpazneb.actividades.controller;

import cl.duoc.mpazneb.actividades.model.Actividad;
import cl.duoc.mpazneb.actividades.model.Dificultad;
import cl.duoc.mpazneb.actividades.model.EstadoActividad;
import cl.duoc.mpazneb.actividades.model.Modulo;
import cl.duoc.mpazneb.actividades.model.Pregunta;
import cl.duoc.mpazneb.actividades.service.ActividadService;
import cl.duoc.mpazneb.actividades.service.PreguntaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService actividadService;
    private final PreguntaService preguntaService;

    public ActividadController(ActividadService actividadService, PreguntaService preguntaService) {
        this.actividadService = actividadService;
        this.preguntaService = preguntaService;
    }

    // GET /api/actividades?modulo=LECTURA&curso=1&dificultad=FACIL&estado=DISPONIBLE (todos opcionales)
    @GetMapping
    public List<Actividad> listar(
            @RequestParam(required = false) Modulo modulo,
            @RequestParam(required = false) Integer curso,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) EstadoActividad estado
    ) {
        return actividadService.filtrar(modulo, curso, dificultad, estado);
    }

    @GetMapping("/{id}")
    public Actividad buscar(@PathVariable Long id) {
        return actividadService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Actividad> crear(@Valid @RequestBody Actividad actividad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actividadService.crear(actividad));
    }

    @PutMapping("/{id}")
    public Actividad actualizar(@PathVariable Long id, @Valid @RequestBody Actividad actividad) {
        return actividadService.actualizar(id, actividad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Preguntas de una actividad ---

    // GET /api/actividades/1/preguntas
    @GetMapping("/{id}/preguntas")
    public List<Pregunta> preguntas(@PathVariable Long id) {
        return preguntaService.deActividad(id);
    }

    // POST /api/actividades/1/preguntas
    @PostMapping("/{id}/preguntas")
    public ResponseEntity<Pregunta> crearPregunta(@PathVariable Long id, @Valid @RequestBody Pregunta pregunta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preguntaService.crear(id, pregunta));
    }
}
