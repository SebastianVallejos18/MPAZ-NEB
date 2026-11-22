package cl.duoc.mpazneb.asignaciones.controller;

import cl.duoc.mpazneb.asignaciones.dto.CrearAsignacionRequest;
import cl.duoc.mpazneb.asignaciones.dto.ObservacionRequest;
import cl.duoc.mpazneb.asignaciones.dto.ProgresoRequest;
import cl.duoc.mpazneb.asignaciones.dto.RespuestaRequest;
import cl.duoc.mpazneb.asignaciones.model.Asignacion;
import cl.duoc.mpazneb.asignaciones.model.Respuesta;
import cl.duoc.mpazneb.asignaciones.service.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService servicio;

    public AsignacionController(AsignacionService servicio) {
        this.servicio = servicio;
    }

    // GET /api/asignaciones?asistenteId=2
    @GetMapping
    public List<Asignacion> listar(@RequestParam(required = false) Long asistenteId) {
        return servicio.deAsistente(asistenteId);
    }

    @GetMapping("/{id}")
    public Asignacion buscar(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    // GET /api/asignaciones/codigo/AB3K7P  -> ingreso del estudiante
    @GetMapping("/codigo/{codigo}")
    public Asignacion buscarPorCodigo(@PathVariable String codigo) {
        return servicio.buscarPorCodigo(codigo);
    }

    // POST /api/asignaciones  -> 201 con la asignación y su código temporal
    @PostMapping
    public ResponseEntity<Asignacion> crear(@Valid @RequestBody CrearAsignacionRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    // PUT /api/asignaciones/1  -> avance de la sesión (estado, intentos, aciertos, errores)
    @PutMapping("/{id}")
    public Asignacion actualizarProgreso(@PathVariable Long id, @Valid @RequestBody ProgresoRequest datos) {
        return servicio.actualizarProgreso(id, datos);
    }

    // PATCH /api/asignaciones/1/observacion
    @PatchMapping("/{id}/observacion")
    public Asignacion guardarObservacion(@PathVariable Long id, @Valid @RequestBody ObservacionRequest datos) {
        return servicio.guardarObservacion(id, datos.observacion());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Respuestas ---

    // GET /api/asignaciones/1/respuestas?intento=1  (intento opcional)
    @GetMapping("/{id}/respuestas")
    public List<Respuesta> respuestas(@PathVariable Long id, @RequestParam(required = false) Integer intento) {
        return servicio.respuestasDe(id, intento);
    }

    // POST /api/asignaciones/1/respuestas  -> guarda la respuesta y devuelve la asignación actualizada
    @PostMapping("/{id}/respuestas")
    public ResponseEntity<Asignacion> registrarRespuesta(@PathVariable Long id,
                                                         @Valid @RequestBody RespuestaRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrarRespuesta(id, datos));
    }
}
