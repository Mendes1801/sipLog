package br.mackenzie.labEngenhariaSW.sipLogBFF.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TokenRelayInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        
        // 1. Pega o usuário autenticado do contexto atual da requisição
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Verifica se existe um usuário e se ele está usando um JWT
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // 3. Injeta o token original no cabeçalho Authorization da nova requisição (para a core-api)
            request.getHeaders().setBearerAuth(jwt.getTokenValue());
        }

        // 4. Segue com a execução da chamada HTTP
        return execution.execute(request, body);
    }
}