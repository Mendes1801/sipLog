package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.RegistroDTO;

@RestController
public class SipBffController {

    private final RestClient restClient;

    // Melhor prática: Injetar o RestClient via construtor
    public SipBffController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(Principal auth) {
        Map<String, Object> dadosUser = new HashMap<>();

        if (auth != null) {
            dadosUser.put("name", auth.getName());
            return ResponseEntity.ok(dadosUser);
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // Rota 1: O Flutter chama para montar a aba "Explore / Global"
    @GetMapping("/feed/global")
    public ResponseEntity<Object> getFeedGlobal(@RequestParam(defaultValue = "0") int pagina) {
        
        // O BFF repassa o pedido de paginação para a Core API
        Object feedDaCoreApi = restClient.get()
                .uri("http://localhost:8082/internal/v1/feed/global?pagina=" + pagina)
                .retrieve()
                .body(Object.class); // Recebe a página estruturada (JSON) e repassa direto
        
        return ResponseEntity.ok(feedDaCoreApi);
    }

    // Rota 2: O Flutter chama para montar a aba "Atelier / Amigos"
    @GetMapping("/feed/amigos")
    public ResponseEntity<Object> getFeedAmigos(
            @AuthenticationPrincipal Jwt jwt, 
            @RequestParam(defaultValue = "0") int pagina) {
        
        String usuarioId = jwt.getSubject(); 

        Object feedDaCoreApi = restClient.get()
                .uri("http://localhost:8082/internal/v1/feed/amigos?pagina=" + pagina)
                .header("X-User-Id", usuarioId) // Enviando a credencial que a Core API exige
                .retrieve()
                .body(Object.class);
        
        return ResponseEntity.ok(feedDaCoreApi);
    }

    @PostMapping(value = "/experiencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registrarExperiencia(
            @RequestPart("dados") RegistroDTO dados,
            @RequestPart("foto") MultipartFile foto,
            @AuthenticationPrincipal Jwt jwt) {

        String usuarioId = jwt.getSubject();

        // Lógica do Upload S3 e encaminhamento para Core API...

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}