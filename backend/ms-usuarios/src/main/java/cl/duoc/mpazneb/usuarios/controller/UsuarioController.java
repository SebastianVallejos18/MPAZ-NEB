package cl.duoc.mpazneb.usuarios.controller;

import cl.duoc.mpazneb.usuarios.dto.LoginRequest;
import cl.duoc.mpazneb.usuarios.dto.UsuarioRequest;
import cl.duoc.mpazneb.usuarios.model.Perfil;
import cl.duoc.mpazneb.usuarios.model.Usuario;
import cl.duoc.mpazneb.usuarios.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    // GET /api/usuarios?perfil=ESTUDIANTE  (el filtro es opcional)
    @GetMapping
    public List<Usuario> listar(@RequestParam(required = false) Perfil perfil) {
        return servicio.listar(perfil);
    }

    // GET /api/usuarios/1
    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    // POST /api/usuarios/login  -> 200 con el usuario, o 401 si los datos no coinciden
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest datos) {
        Usuario usuario = servicio.login(datos.alias(), datos.clave());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(usuario);
    }

    // PATCH /api/usuarios/1/acceso  -> actualiza la fecha de último acceso (ingreso del estudiante)
    @PatchMapping("/{id}/acceso")
    public Usuario registrarAcceso(@PathVariable Long id) {
        return servicio.registrarAcceso(id);
    }

    // POST /api/usuarios  -> 201 con el usuario creado
    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    // PUT /api/usuarios/1
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest datos) {
        return servicio.actualizar(id, datos);
    }

    // DELETE /api/usuarios/1  -> 204 sin contenido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
