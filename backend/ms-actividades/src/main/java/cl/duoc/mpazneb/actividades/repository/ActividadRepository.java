package cl.duoc.mpazneb.actividades.repository;

import cl.duoc.mpazneb.actividades.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    // Todas las actividades ordenadas por curso y luego por nombre
    List<Actividad> findAllByOrderByCursoRecomendadoAscNombreAsc();
}
