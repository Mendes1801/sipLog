package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.FeedItemDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.FeedResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.service.FeedBffService;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedBffController {

    private final FeedBffService feedService;
    private final RestClient restCLient;

    public FeedBffController(FeedBffService feedService, RestClient restClient) {
        this.feedService = feedService;
        this.restCLient = restClient;
    }

    // Rota 1: O Flutter chama para montar a aba "Explore / Global"
    @GetMapping("/global")
    public ResponseEntity<PaginaBffDTORecive<FeedResponseDTO>> getFeedGlobal(@RequestParam(defaultValue = "0") int pagina) {
        
        // 1. O BFF pede a página para a Core API
        PaginaBffDTORecive<FeedItemDTORecive> feedApi = restCLient.get()
                .uri("http://localhost:8082/internal/v1/feed/global?pagina=" + pagina)
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<FeedItemDTORecive>>() {}); 
        
        // 2. Transforma o DTO da Core API no DTO do Flutter (Mastigando os dados)
        List<FeedResponseDTO> feedFormatado = feedApi.content().stream()
                .map(item -> {
                    // Exemplo de cálculo de tempo decorrido (em horas)
                    // 1. Primeiro convertemos a String de volta para LocalDateTime
                    LocalDateTime dataDoPost = LocalDateTime.parse(item.experiencia().data());

                    //Agora o cálculo de duração vai funcionar perfeitamente!
                    long horasAtras = Duration.between(dataDoPost, LocalDateTime.now()).toHours();
                    String tempoDecorrido = horasAtras == 0 ? "Agora mesmo" : "Há " + horasAtras + "h";

                    return new FeedResponseDTO(
                            item.idPost(), 
                            item.autor().idUsuario(), 
                            item.autor().nome(), 
                            item.autor().fotoAvatarUrl(), 
                            tempoDecorrido, 
                            item.experiencia().local(), 
                            item.experiencia().fotoPostUrl(), 
                            item.bebida().idBebida(), 
                            item.bebida().nome(), 
                            item.bebida().categoria(), 
                            item.experiencia().nota(), 
                            item.experiencia().comentario(),
                            item.engajamento().curtidoPorMim(), 
                            item.engajamento().totalCurtidas(), 
                            item.engajamento().totalComentarios()
                    );
                })
                .toList();
        
        // 3. O PASSO MAIS IMPORTANTE: Remonta a página com os novos dados formatados
        PaginaBffDTORecive<FeedResponseDTO> paginaParaOFlutter = new PaginaBffDTORecive<>(
                feedFormatado, // A nova lista
                feedApi.number(), // Repassa a página atual
                feedApi.size(),   // Repassa o tamanho
                feedApi.totalElements(), // Repassa o total de posts
                feedApi.totalPages(),    // Repassa o total de páginas
                feedApi.first(),         // Repassa se é a primeira
                feedApi.last()           // Repassa se é a última (Flutter usa isso para parar de carregar)
        );
        
        // 4. Devolve a página completa e mastigada para o mobile
        return ResponseEntity.ok(paginaParaOFlutter);
    }



    @GetMapping("/amigos")
    public ResponseEntity<Object> getFeedAmigos(
                    @AuthenticationPrincipal Jwt jwt,
                    @RequestParam(defaultValue = "0") int pagina) {

            String usuarioId = jwt.getSubject();

            Object feedDaCoreApi = restCLient.get()
                            .uri("http://localhost:8082/internal/v1/feed/amigos?pagina=" + pagina)
                            .header("X-User-Id", usuarioId) // Enviando a credencial que a Core API exige
                            .retrieve()
                            .body(Object.class);

            return ResponseEntity.ok(feedDaCoreApi);
    }

    // ITEM 2: Feed de Terceiros (Quando clico no perfil de alguém)
    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<PaginaBffDTORecive<FeedResponseDTO>> getFeedDeUsuario(
            @PathVariable Long idUsuario, 
            @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt principal) {
        
        PaginaBffDTORecive<FeedResponseDTO> feed = feedService.buscarFeedDeTerceiro(idUsuario, pagina, principal.getSubject());
        return ResponseEntity.ok(feed);
    }
}