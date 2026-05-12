package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.FeedResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.service.FeedBffService;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedBffController {

    private final FeedBffService feedService;

    public FeedBffController(FeedBffService feedService) {
        this.feedService = feedService;
    }

    // Rotas existentes... (Global, Amigos, Me)

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