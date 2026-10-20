package pe.edu.cibertec.sigip.common.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.cibertec.sigip.common.error.GlobalExceptionHandler;
import pe.edu.cibertec.sigip.common.error.RespuestasSeguridad;

@AutoConfiguration(before = SecurityAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SigipSecurityProperties.class)
@EnableMethodSecurity
@Import(GlobalExceptionHandler.class)
public class SigipSecurityAutoConfiguration {
    static final String[] RUTAS_MONITOREO = {
            "/actuator/health", "/actuator/health/**", "/actuator/info", "/actuator/prometheus", "/error"
    };

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService(SigipSecurityProperties properties) {
        return new JwtService(properties.getJwt().getSecret(), properties.getJwt().getExpirationMinutes());
    }

    @Bean
    @ConditionalOnMissingBean
    public FiltroJwtAuth filtroJwtAuth(JwtService jwtService) { return new FiltroJwtAuth(jwtService); }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService sinUsuariosLocales() {
        return username -> { throw new UsernameNotFoundException(
                "Este servicio no autentica usuarios; utilice auth-service"); };
    }

    @Bean
    public FilterRegistrationBean<FiltroJwtAuth> filtroJwtAuthRegistration(FiltroJwtAuth filtro) {
        FilterRegistrationBean<FiltroJwtAuth> registration = new FilterRegistrationBean<>(filtro);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain sigipSecurityFilterChain(HttpSecurity http, FiltroJwtAuth filtro,
                                                        SigipSecurityProperties properties) throws Exception {
        String[] publicas = properties.getSecurity().getPublicPaths().toArray(String[]::new);
        String[] publicasGet = properties.getSecurity().getPublicGetPaths().toArray(String[]::new);
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(RespuestasSeguridad::noAutenticado)
                        .accessDeniedHandler(RespuestasSeguridad::accesoDenegado))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(RUTAS_MONITOREO).permitAll();
                    if (publicas.length > 0) auth.requestMatchers(publicas).permitAll();
                    if (publicasGet.length > 0) auth.requestMatchers(HttpMethod.GET, publicasGet).permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(filtro, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
