package pe.edu.cibertec.sigip.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Identidad obtenida del JWT. Además del indicador ADMIN conserva los roles para
 * aplicar reglas de negocio internas sin depender de la interfaz web.
 */
public record UsuarioActual(String nombre, boolean esAdmin, Set<String> roles) {

    /** Compatibilidad con pruebas y código anterior que solo enviaba nombre/admin. */
    public UsuarioActual(String nombre, boolean esAdmin) {
        this(nombre, esAdmin, esAdmin ? Set.of("ADMIN") : Set.of());
    }

    public UsuarioActual {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public static UsuarioActual de(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .collect(Collectors.toUnmodifiableSet());
        return new UsuarioActual(authentication.getName(), roles.contains("ADMIN"), roles);
    }

    public boolean tieneRol(String rol) {
        return roles.contains(rol);
    }

    public boolean esVentas() {
        return tieneRol("VENTAS");
    }

    public boolean esOperaciones() {
        return tieneRol("OPERACIONES");
    }

    /** Rol antiguo conservado temporalmente solo para no romper datos/pruebas históricas. */
    public boolean esClienteLegacy() {
        return tieneRol("CLIENTE");
    }

    public boolean esInterno() {
        return esAdmin || esVentas() || esOperaciones();
    }
}
