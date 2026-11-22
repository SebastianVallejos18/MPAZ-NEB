package cl.duoc.mpazneb.asignaciones.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RespuestaRequest(
        @NotNull(message = "Falta la pregunta") Long preguntaId,
        @Min(1) int intento,
        @Min(0) @Max(2) int opcionElegida,
        boolean esCorrecta
) {
}
