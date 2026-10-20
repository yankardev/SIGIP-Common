package pe.edu.cibertec.sigip.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class JwtService {
    private static final String CLAIM_AUTHORITIES = "authorities";
    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(String secretBase64, long expirationMinutes) {
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException("Debe configurar sigip.jwt.secret con una clave Base64");
        }
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expirationMillis = expirationMinutes * 60_000L;
    }

    public String generarToken(String username, Collection<String> authorities) {
        Instant now = Instant.now();
        return Jwts.builder().subject(username).claim(CLAIM_AUTHORITIES, List.copyOf(authorities))
                .issuedAt(Date.from(now))
                .expiration(new Date(now.toEpochMilli() + expirationMillis))
                .signWith(key).compact();
    }

    public String obtenerUsername(String token) { return obtenerClaims(token).getSubject(); }

    public List<String> obtenerAuthorities(String token) {
        Object value = obtenerClaims(token).get(CLAIM_AUTHORITIES);
        if (value instanceof List<?> list) return list.stream().map(String::valueOf).toList();
        return List.of();
    }

    public boolean tokenValido(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.getExpiration() != null
                    && claims.getExpiration().getTime() > System.currentTimeMillis();
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
