package cl.duoc.mpazneb.asignaciones.dto;

import cl.duoc.mpazneb.asignaciones.model.EstadoAsignacion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Avance de la sesión del estudiante: estado e intentos/aciertos/errores del intento actual
public record ProgresoRequest(
        @NotNull(message = "Falta el estado") EstadoAsignacion estado,
        @Min(0) int intentos,
        @Min(0) int aciertos,
        @Min(0) int errores
) {
}
