package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Notificacao;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.TipoNotificacao;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.event.NotificacaoEvent;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.AcessoNegadoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.RecursoNaoEncontradoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.NotificacaoRepository;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoCoreService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioCoreService usuarioService;

    public NotificacaoCoreService(NotificacaoRepository notificacaoRepository, UsuarioCoreService usuarioService) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioService = usuarioService;
    }

    @EventListener
    @Async // Salva em backgroud
    public void onNotificacaoEvent(NotificacaoEvent event) {
        
        // A nossa regra de ouro da notificação
        if (event.ator().getId().equals(event.recebedor().getId())) {
            return;
        }

        Notificacao nova = new Notificacao();
        nova.setAtor(event.ator());
        nova.setRecebedor(event.recebedor());
        nova.setTipo(event.tipo());
        nova.setReferenciaId(event.referenciaId());
        
        notificacaoRepository.save(nova);
    }


    public Page<Notificacao> listarMinhasNotificacoes(String keycloakId, int pagina) {
        Usuario eu = usuarioService.getUsuarioPerfil(keycloakId);
        Pageable paginacao = PageRequest.of(pagina, 20);
        return notificacaoRepository.findByRecebedorIdOrderByDataCriacaoDesc(eu.getId(), paginacao);
    }

    public long contarNaoLidas(String keycloakId) {
        Usuario eu = usuarioService.getUsuarioPerfil(keycloakId);
        return notificacaoRepository.countByRecebedorIdAndLidaFalse(eu.getId());
    }

    @Transactional
    public void marcarComoLida(Long idNotificacao, String keycloakId) {
        Notificacao notif = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada."));

        // Segurança: O usuário só pode marcar como lida as SUAS notificações
        if (!notif.getRecebedor().getKeycloakId().equals(keycloakId)) {
            throw new AcessoNegadoException("Você não tem permissão sobre esta notificação.");
        }

        notif.setLida(true);
        notificacaoRepository.save(notif);
    }

    // ========================================== AUXILIAR ========================================


    @Transactional
    public void gerarNotificacao(Usuario ator, Usuario recebedor, TipoNotificacao tipo, Long referenciaId) {
        // Regra de Ouro: Nunca notificamos a nós mesmos (ex: se eu curtir meu próprio post)
        if (ator.getId().equals(recebedor.getId())) {
            return;
        }

        Notificacao nova = new Notificacao();
        nova.setAtor(ator);
        nova.setRecebedor(recebedor);
        nova.setTipo(tipo);
        nova.setReferenciaId(referenciaId);
        
        notificacaoRepository.save(nova);
    }
}