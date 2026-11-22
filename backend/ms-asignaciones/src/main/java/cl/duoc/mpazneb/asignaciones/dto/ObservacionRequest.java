package cl.duoc.mpazneb.asignaciones.dto;

import jakarta.validation.constraints.Size;

public record ObservacionRequest(
        @Size(max = 300, message = "No puede superar los 300 caracteres") String observacion
) {
}
