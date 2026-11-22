package cl.duoc.mpazneb.asignaciones.repository;

import cl.duoc.mpazneb.asignaciones.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // Devuelve null si no existe una asignación con ese código
    Asignacion findByCodigoTemporal(String codigoTemporal);

    boolean existsByCodigoTemporal(String codigoTemporal);

    List<Asignacion> findByAsistenteIdOrderByFechaDesc(Long asistenteId);
}
