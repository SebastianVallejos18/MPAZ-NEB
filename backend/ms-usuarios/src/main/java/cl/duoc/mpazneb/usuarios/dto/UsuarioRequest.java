package cl.duoc.mpazneb.usuarios.dto;

import cl.duoc.mpazneb.usuarios.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Datos para crear o editar un usuario. La clave es opcional al editar.
public record UsuarioRequest(
        @NotBlank(message = "El alias es obligatorio")
        @Size(min = 3, max = 20, message = "Debe tener entre 3 y 20 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Usa solo letras, números o guion bajo")
        String alias,
        String clave,
        @NotNull(message = "El perfil es obligatorio") Perfil perfil,
        Boolean activo
) {
}
