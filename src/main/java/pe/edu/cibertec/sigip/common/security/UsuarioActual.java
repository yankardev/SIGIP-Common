package pe.edu.cibertec.sigip.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Usuario autenticado tomado del JWT.
 *
 * CLIENTE representa al usuario externo. ADMIN, VENTAS y OPERACIONES son perfiles internos.
 * Se conserva el constructor de dos parámetros para no romper pruebas existentes.
 */
public record UsuarioActual(String nombre, boolean esAdmin, boolean esInterno, Set<String> roles) {

    public UsuarioActual(String nombre, boolean esAdmin) {
        this(nombre, esAdmin, esAdmin, esAdmin ? Set.of("ADMIN") : Set.of("CLIENTE"));
    }

    public static UsuarioActual de(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .collect(Collectors.toUnmodifiableSet());

        boolean admin = roles.contains("ADMIN");
        boolean interno = admin || roles.contains("VENTAS") || roles.contains("OPERACIONES");
        return new UsuarioActual(authentication.getName(), admin, interno, roles);
    }

    public boolean tieneRol(String rol) {
        return roles.contains(rol);
    }

    public boolean esCliente() {
        return roles.contains("CLIENTE") && !esInterno;
    }
}
