package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ContagemNotificacoesDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.NotificacaoResponseDTO;

@Service
public class NotificacaoBffService {

    public PaginaBffDTORecive<NotificacaoResponseDTO> buscarNotificacoes(String subject, int pagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarNotificacoes'");
    }

    public void marcarComoLida(Long id, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'marcarComoLida'");
    }

    public ContagemNotificacoesDTO contarNaoLidas(String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contarNaoLidas'");
    }

}
