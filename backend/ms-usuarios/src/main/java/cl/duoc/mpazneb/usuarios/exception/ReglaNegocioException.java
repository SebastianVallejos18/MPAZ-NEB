package cl.duoc.mpazneb.usuarios.exception;

// Se lanza cuando una operación rompe una regla del caso (responde 409)
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
