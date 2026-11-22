package cl.duoc.mpazneb.actividades.controller;

import cl.duoc.mpazneb.actividades.model.Pregunta;
import cl.duoc.mpazneb.actividades.service.PreguntaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final PreguntaService servicio;

    public PreguntaController(PreguntaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/{id}")
    public Pregunta buscar(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Pregunta actualizar(@PathVariable Long id, @Valid @RequestBody Pregunta pregunta) {
        return servicio.actualizar(id, pregunta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
