package cl.duoc.mpazneb.usuarios.repository;

import cl.duoc.mpazneb.usuarios.model.Perfil;
import cl.duoc.mpazneb.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Spring Data genera el SQL a partir del nombre de cada método
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Devuelve null si no existe un usuario con ese alias
    Usuario findByAlias(String alias);

    List<Usuario> findByPerfilOrderByAliasAsc(Perfil perfil);

    boolean existsByAlias(String alias);
}
