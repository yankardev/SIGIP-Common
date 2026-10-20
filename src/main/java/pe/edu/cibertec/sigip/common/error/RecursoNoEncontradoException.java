package pe.edu.cibertec.sigip.common.error;

/** El recurso solicitado no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) { super(message); }
}
