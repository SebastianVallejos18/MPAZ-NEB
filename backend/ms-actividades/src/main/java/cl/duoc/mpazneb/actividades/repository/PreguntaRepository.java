package cl.duoc.mpazneb.actividades.repository;

import cl.duoc.mpazneb.actividades.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    // Consultas escritas en JPQL: recorren la relación pregunta.actividad.id
    @Query("SELECT p FROM Pregunta p WHERE p.actividad.id = :actividadId ORDER BY p.id ASC")
    List<Pregunta> findByActividadIdOrderByIdAsc(@Param("actividadId") Long actividadId);

    @Query("SELECT COUNT(p) FROM Pregunta p WHERE p.actividad.id = :actividadId")
    long countByActividadId(@Param("actividadId") Long actividadId);
}
