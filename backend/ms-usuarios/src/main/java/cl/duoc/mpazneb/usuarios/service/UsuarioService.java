package cl.duoc.mpazneb.usuarios.service;

import cl.duoc.mpazneb.usuarios.dto.UsuarioRequest;
import cl.duoc.mpazneb.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.mpazneb.usuarios.exception.ReglaNegocioException;
import cl.duoc.mpazneb.usuarios.model.Perfil;
import cl.duoc.mpazneb.usuarios.model.Usuario;
import cl.duoc.mpazneb.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Aquí vive la lógica de negocio; el controlador solo recibe y responde
@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Usuario> listar(Perfil perfil) {
        return perfil == null ? repositorio.findAll() : repositorio.findByPerfilOrderByAliasAsc(perfil);
    }

    public Usuario buscarPorId(Long id) {
        // findById devuelve un Optional (una "caja" que puede venir vacía); orElse(null) la abre
        Usuario usuario = repositorio.findById(id).orElse(null);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("No existe el usuario " + id);
        }
        return usuario;
    }

    // Devuelve el usuario si el alias y la clave coinciden y la cuenta está activa; si no, null
    public Usuario login(String alias, String clave) {
        Usuario usuario = repositorio.findByAlias(alias);
        if (usuario == null || !usuario.isActivo()) {
            return null;
        }
        // Se compara el hash de la clave escrita con el hash guardado
        if (!usuario.getClaveHash().equals(HashUtil.sha256(clave))) {
            return null;
        }
        return marcarAcceso(usuario);
    }

    public Usuario registrarAcceso(Long id) {
        return marcarAcceso(buscarPorId(id));
    }

    public Usuario crear(UsuarioRequest datos) {
        if (repositorio.existsByAlias(datos.alias())) {
            throw new ReglaNegocioException("El alias " + datos.alias() + " ya está en uso");
        }
        // Los estudiantes no tienen clave: entran con el código temporal
        boolean necesitaClave = datos.perfil() != Perfil.ESTUDIANTE;
        if (necesitaClave && (datos.clave() == null || datos.clave().length() < 6)) {
            throw new ReglaNegocioException("La contraseña debe tener al menos 6 caracteres");
        }
        Usuario usuario = new Usuario();
        usuario.setAlias(datos.alias());
        usuario.setPerfil(datos.perfil());
        usuario.setActivo(datos.activo() == null || datos.activo());
        usuario.setClaveHash(necesitaClave ? HashUtil.sha256(datos.clave()) : "");
        return repositorio.save(usuario);
    }

    public Usuario actualizar(Long id, UsuarioRequest datos) {
        Usuario usuario = buscarPorId(id);
        boolean cambiaAlias = !usuario.getAlias().equals(datos.alias());
        if (cambiaAlias && repositorio.existsByAlias(datos.alias())) {
            throw new ReglaNegocioException("El alias " + datos.alias() + " ya está en uso");
        }
        usuario.setAlias(datos.alias());
        usuario.setPerfil(datos.perfil());
        if (datos.activo() != null) {
            usuario.setActivo(datos.activo());
        }
        // Si no se envía clave, se mantiene la anterior
        if (datos.clave() != null && !datos.clave().isBlank()) {
            usuario.setClaveHash(HashUtil.sha256(datos.clave()));
        }
        return repositorio.save(usuario);
    }

    public void eliminar(Long id) {
        repositorio.delete(buscarPorId(id));
    }

    private Usuario marcarAcceso(Usuario usuario) {
        usuario.setUltimoAcceso(System.currentTimeMillis());
        return repositorio.save(usuario);
    }
}
