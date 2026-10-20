package pe.edu.cibertec.sigip.common.error;

/** Una operación viola una regla de negocio. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String message) { super(message); }
}
