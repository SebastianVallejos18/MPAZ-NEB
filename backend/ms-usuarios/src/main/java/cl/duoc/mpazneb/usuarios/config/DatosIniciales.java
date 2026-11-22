package cl.duoc.mpazneb.usuarios.config;

import cl.duoc.mpazneb.usuarios.model.Perfil;
import cl.duoc.mpazneb.usuarios.model.Usuario;
import cl.duoc.mpazneb.usuarios.repository.UsuarioRepository;
import cl.duoc.mpazneb.usuarios.service.HashUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Carga los usuarios ficticios de prueba solo si la tabla está vacía
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository repositorio;

    public DatosIniciales(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }
        guardar("admin", HashUtil.sha256("admin123"), Perfil.ADMINISTRADOR);
        guardar("asistente1", HashUtil.sha256("asist123"), Perfil.ASISTENTE);
        // Estudiantes ficticios: sin clave, solo entran con el código temporal
        guardar("estudiante1", "", Perfil.ESTUDIANTE);
        guardar("estudiante2", "", Perfil.ESTUDIANTE);
        guardar("estudiante3", "", Perfil.ESTUDIANTE);
    }

    private void guardar(String alias, String claveHash, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setAlias(alias);
        usuario.setClaveHash(claveHash);
        usuario.setPerfil(perfil);
        usuario.setActivo(true);
        usuario.setUltimoAcceso(System.currentTimeMillis());
        repositorio.save(usuario);
    }
}
