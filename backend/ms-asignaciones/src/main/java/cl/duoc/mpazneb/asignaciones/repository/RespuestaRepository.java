package cl.duoc.mpazneb.asignaciones.repository;

import cl.duoc.mpazneb.asignaciones.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // Consultas escritas en JPQL: recorren la relación respuesta.asignacion.id
    @Query("SELECT r FROM Respuesta r WHERE r.asignacion.id = :asignacionId ORDER BY r.intento ASC, r.id ASC")
    List<Respuesta> findByAsignacionIdOrderByIntentoAscIdAsc(@Param("asignacionId") Long asignacionId);

    @Query("SELECT r FROM Respuesta r WHERE r.asignacion.id = :asignacionId AND r.intento = :intento ORDER BY r.id ASC")
    List<Respuesta> findByAsignacionIdAndIntentoOrderByIdAsc(@Param("asignacionId") Long asignacionId,
                                                             @Param("intento") int intento);
}
