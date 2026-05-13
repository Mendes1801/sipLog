package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ContagemNotificacoesDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.NotificacaoResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.service.NotificacaoBffService;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoBffController {
    

    private final NotificacaoBffService notificacaoService;

    public NotificacaoBffController(NotificacaoBffService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    //Notificaões de curtidas de experiencias (ex. joao curtiu seu post), entre outras coisas, como notificações de novos seguidores, etc.

    //GET /api/v1/notificacoes
    //Buscar a lista de notificações
    @GetMapping
    public  ResponseEntity<PaginaBffDTORecive<NotificacaoResponseDTO>> getNotificacoes(
            @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt principal
    ) {

        PaginaBffDTORecive<NotificacaoResponseDTO> notificacoes = notificacaoService.buscarNotificacoes(principal.getSubject(), pagina);
        return ResponseEntity.ok(notificacoes);
    }

    

    //PATCH /api/v1/notificacoes/{id}/lida
    //Marcar uma notificação específica como lida
    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(
            @PathVariable Long id, 
            @AuthenticationPrincipal Jwt principal) {
        
        notificacaoService.marcarComoLida(id, principal.getSubject());
        return ResponseEntity.ok().build();
    }



    //Pegar o total de não lidas (Para a bolinha vermelha no App)
    @GetMapping("/nao-lidas/count")
    public ResponseEntity<ContagemNotificacoesDTO> contarNaoLidas(@AuthenticationPrincipal Jwt principal) {
        
        ContagemNotificacoesDTO contagem = notificacaoService.contarNaoLidas(principal.getSubject());
        return ResponseEntity.ok(contagem);
    }

}
