package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.ParameterizedTypeReference;

import br.mackenzie.labEngenhariaSW.sipLogBFF.model.FeedItemDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.model.RegistroDTO;

@RestController
public class SipBffController {


   @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(Principal auth) {
        Map<String, Object> dadosUser = new HashMap<>();

        if (auth != null) {
            dadosUser.put("name", auth.getName());
            return ResponseEntity.ok(dadosUser);
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<FeedItemDTO>> getFeedSocial(@AuthenticationPrincipal Jwt jwt) {
            
        // Pega o ID único do usuário logado direto do token do Keycloak (geralmente o campo 'sub')
        String usuarioId = jwt.getSubject(); 

        // TODO: O BFF agora faria uma chamada (via RestClient ou Feign) 
        // para o Microserviço Core pedindo o feed do 'usuarioId'.
        
        // Retorno mockado para exemplo
        List<FeedItemDTO> feed = List.of(
            new FeedItemDTO(1L, "Gabriel", "Vinho Tinto Reservado", 4.5, "https://s3.aws.com/foto1.jpg")
        );

        return ResponseEntity.ok(feed);
    }

    @PostMapping(value = "/experiencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registrarExperiencia(
            @RequestPart("dados") RegistroDTO dados,
            @RequestPart("foto") MultipartFile foto,
            @AuthenticationPrincipal Jwt jwt) {

        String usuarioId = jwt.getSubject();

        // O BFF orquestra o fluxo complexo:
        // 1. Pega o 'MultipartFile', comprime (opcional) e envia para o Amazon S3.
        // 2. Recebe a URL da foto salva no S3.
        // 3. Monta o objeto final e envia para o Microserviço Core salvar no PostgreSQL.

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Object>> getFeedSocial(@AuthenticationPrincipal Jwt jwt, RestClient restClient) {
        
        String usuarioId = jwt.getSubject(); 

        // Olha que limpo! Não precisa mais passar o header aqui. 
        // O RestClient vai chamar o TokenRelayInterceptor que acabamos de criar, 
        // e ele vai injetar o token sozinho!
        List<Object> feedDaCoreApi = restClient.get()
                .uri("http://localhost:8082/internal/v1/feed/" + usuarioId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Object>>() {});
        return ResponseEntity.ok(feedDaCoreApi);
    }

}

