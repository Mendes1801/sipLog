package br.mackenzie.labEngenhariaSW.sipLogBFF.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
        .csrf(csrf -> csrf.disable())

        .authorizeHttpRequests(req -> req
            .requestMatchers("/", "/public/**", "/login", "/favicon.ico", "/error", "/me").permitAll()
            .anyRequest().authenticated())
            .oauth2Login(auth -> auth
                .loginPage("/login")
                .defaultSuccessUrl("http://localhost:5173/home", true)
                
                // FAILURE HANDLER: Captures exceptions that occur during the login handshake phase
                .failureHandler((request, response, exception) -> {
                    logger.info(">>>> OAuth2 Flow Failure. Cleaning up and redirecting to Vue...");
                    
                    // Se tiver lixo de sessão dessa aba velha, nós limpamos
                    if (request.getSession(false) != null) {
                        request.getSession(false).invalidate();
                    }
                    
                    // Response UNAUTHORIZED
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }))
            .logout(log -> log
                .logoutSuccessHandler(oidcLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("SESSION")
            ).build();
    }


    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);

        // Define para onde o Keycloak deve devolver o usuário após encerrar a sessão lá
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("http://localhost:5173/");

        // Se a sessão já tinha morrido, manda direto pro Vue
        oidcLogoutSuccessHandler.setDefaultTargetUrl("http://localhost:5173/");

        return oidcLogoutSuccessHandler;
    }
}