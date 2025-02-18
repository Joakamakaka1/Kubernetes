package com.es.api_ciervus.security;

import com.es.api_ciervus.error.exception.InternalServerErrorException;
import com.es.api_ciervus.model.Producto;
import com.es.api_ciervus.model.Reserva;
import com.es.api_ciervus.model.Usuario;
import com.es.api_ciervus.repository.ProductoRepository;
import com.es.api_ciervus.repository.ReservaRepository;
import com.es.api_ciervus.repository.UsuarioRepository;
import com.es.api_ciervus.utils.StringToLong;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private RsaKeyProperties rsaKeys;

    @Autowired
    private StringToLong stl;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //Usuarios
                        .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/register").permitAll()//todos
                        .requestMatchers(HttpMethod.GET, "/usuarios/{id}").access(getUserIdManager())//admin y el propio user
                        .requestMatchers(HttpMethod.GET, "/usuarios/").hasRole("ADMIN")//Solo admin
                        .requestMatchers(HttpMethod.PUT, "/usuarios/{id}").access(getUserIdManager())
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/{id}").access(getUserIdManager())
                        //Productos
                        .requestMatchers(HttpMethod.POST, "/productos/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/productos/{id}").access(getProductosIdManager())
                        .requestMatchers(HttpMethod.DELETE, "/productos/{id}").access(getProductosIdManager())
                        //Reservas
                        .requestMatchers(HttpMethod.POST, "/reservas/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reservas/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservas/{id}").access(getReservasIdManager())
                        .requestMatchers(HttpMethod.PUT, "/reservas/{id}").access(getReservasIdManager())
                        .requestMatchers(HttpMethod.DELETE, "/reservas/{id}").access(getReservasIdManager())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .httpBasic(Customizer.withDefaults())
                .build();
    }


    public AuthorizationManager<RequestAuthorizationContext> getProductosIdManager() {
        return (authentication, object) -> {
            Authentication auth = authentication.get();

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return new AuthorizationDecision(true);
            }

            String path = object.getRequest().getRequestURI();
            String idString = path.replaceAll("/productos/", "");
            Long id = stl.stringToLong(idString);

            if(id == null) {
                return new AuthorizationDecision(false);
            }

            Producto producto = null;
            try {
                producto = productoRepository.findById(id).orElse(null);
            } catch (Exception e) {
                throw new InternalServerErrorException("Error inesperado: " + e.getMessage());
            }

            if (producto == null) {
                return new AuthorizationDecision(false);
            }

            Long idProducto = producto.getPropietario().getId();
            Usuario user = usuarioRepository.findById(id).orElse(null);

            if (user == null) {
                return new AuthorizationDecision(false);
            }

            if (!producto.getPropietario().getUsername().equals(auth.getName())) {
                return new AuthorizationDecision(false);
            }

            return new AuthorizationDecision(true);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> getReservasIdManager() {
        return (authentication, object) -> {
            Authentication auth = authentication.get();

            if (auth == null || !auth.isAuthenticated()){
                return new AuthorizationDecision(false);
            }

            // Validar si es administrador
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return new AuthorizationDecision(true);
            }

            // Obtener ID de la reserva desde la URI
            String path = object.getRequest().getRequestURI();

            String idString = path.replaceAll("/reservas/", "");
            Long id = stl.stringToLong(idString);

            if (id == null) {
                 return new AuthorizationDecision(false);
            }

            // Buscar la reserva
            Reserva reserva = reservaRepository.findById(id).orElse(null);

            if (reserva == null) {
                return new AuthorizationDecision(false);
            }

            // Verificar que el usuario de la reserva coincide con el autenticado
            Usuario usuarioReserva = reserva.getUsuario();
            if (usuarioReserva == null || !usuarioReserva.getUsername().equals(auth.getName())) {
                return new AuthorizationDecision(false);
            }

            return new AuthorizationDecision(true);
        };
    }


    public AuthorizationManager<RequestAuthorizationContext> getUserIdManager() {
        return (authentication, object) -> {
            Authentication auth = authentication.get();

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return new AuthorizationDecision(true);
            }

            String path = object.getRequest().getRequestURI();
            String idString = path.replaceAll("/usuarios/", "");
            Long id = stl.stringToLong(idString);

            if(id == null) {
                return new AuthorizationDecision(false);
            }

            Usuario usuario = null;
            try {
                usuario = usuarioRepository.findById(id).orElse(null);
            } catch (Exception e) {
                throw new InternalServerErrorException("Error inesperado: " + e.getMessage());
            }

            if (usuario == null) {
                return new AuthorizationDecision(false);
            }
            if (!usuario.getUsername().equals(auth.getName())) {
                return new AuthorizationDecision(false);
            }

            return new AuthorizationDecision(true);
        };
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Decodificador
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    //Codificador
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
