package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;


import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.ContagemNotificacoesDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.NotificacaoResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Notificacao;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.NotificacaoCoreService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/v1/notificacoes")
public class NotificacaoCoreController {

    private final NotificacaoCoreService notificacaoService;

    public NotificacaoCoreController(NotificacaoCoreService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    // Endpoint para listar minhas notificações (paginado)
    @GetMapping
    public ResponseEntity<Page<NotificacaoResponseDTO>> listarNotificacoes(
            @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt principal) {

        Page<Notificacao> entidades = notificacaoService.listarMinhasNotificacoes(principal.getSubject(), pagina);

        Page<NotificacaoResponseDTO> dtos = entidades.map(n -> new NotificacaoResponseDTO(
                n.getId(),
                n.getAtor().getNome(),
                n.getAtor().getFotoAvatarUrl(),
                n.getTipo().name(),
                n.getReferenciaId(),
                n.isLida(),
                n.getDataCriacao().toString()
        ));

        return ResponseEntity.ok(dtos);
    }

    // Endpoint para marcar uma notificação como lida
    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(
            @PathVariable Long id, 
            @AuthenticationPrincipal Jwt principal) {
        notificacaoService.marcarComoLida(id, principal.getSubject());
        return ResponseEntity.ok().build();
    }

    // Endpoint para contar notificações não lidas (usado para mostrar badge no app)
    @GetMapping("/nao-lidas/count")
    public ResponseEntity<ContagemNotificacoesDTO> contarNaoLidas(@AuthenticationPrincipal Jwt principal) {
        long contagem = notificacaoService.contarNaoLidas(principal.getSubject());
        return ResponseEntity.ok(new ContagemNotificacoesDTO((int) contagem));
    }
}