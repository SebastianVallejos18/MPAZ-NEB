package cl.duoc.mpazneb.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

// Cuerpo que envía la app al iniciar sesión
public record LoginRequest(
        @NotBlank(message = "Ingresa el alias") String alias,
        @NotBlank(message = "Ingresa la contraseña") String clave
) {
}
