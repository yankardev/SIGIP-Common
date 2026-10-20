package pe.edu.cibertec.sigip.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "sigip")
public class SigipSecurityProperties {
    private final Jwt jwt = new Jwt();
    private final Security security = new Security();

    public Jwt getJwt() { return jwt; }
    public Security getSecurity() { return security; }

    public static class Jwt {
        private String secret;
        private long expirationMinutes = 30;
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpirationMinutes() { return expirationMinutes; }
        public void setExpirationMinutes(long expirationMinutes) { this.expirationMinutes = expirationMinutes; }
    }

    public static class Security {
        private List<String> publicPaths = new ArrayList<>();
        private List<String> publicGetPaths = new ArrayList<>();
        public List<String> getPublicPaths() { return publicPaths; }
        public void setPublicPaths(List<String> publicPaths) { this.publicPaths = publicPaths; }
        public List<String> getPublicGetPaths() { return publicGetPaths; }
        public void setPublicGetPaths(List<String> publicGetPaths) { this.publicGetPaths = publicGetPaths; }
    }
}
