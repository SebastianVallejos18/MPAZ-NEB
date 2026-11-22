package cl.duoc.mpazneb.asignaciones.dto;

import jakarta.validation.constraints.NotNull;

// Lo que envía la asistente al asignar una actividad. El código lo genera el servidor.
public record CrearAsignacionRequest(
        @NotNull(message = "Falta la actividad") Long actividadId,
        @NotNull(message = "Falta el estudiante") Long estudianteId,
        @NotNull(message = "Falta la asistente") Long asistenteId
) {
}
