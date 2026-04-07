package br.mackenzie.labEngenhariaSW.sipLogBFF.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(TokenRelayInterceptor tokenRelayInterceptor) {
        
        return RestClient.builder()
            .requestInterceptor(tokenRelayInterceptor) 
            .build();
             
    }
}