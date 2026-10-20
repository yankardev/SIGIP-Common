package pe.edu.cibertec.sigip.common.error;

/** Una dependencia necesaria no está disponible. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String message) { super(message); }
}
