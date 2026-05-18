package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ContagemNotificacoesDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.NotificacaoResponseDTO;

@Service
public class NotificacaoBffService {

    @Value("${api.core.base-url}")
    private String apiCoreBaseUrl;

    private final RestClient restClient;

    public NotificacaoBffService(RestClient restClient) {
        this.restClient = restClient;
    }

    //Buscar Notificações (Paginado)
    //Traz o histórico de notificações do usuário logado
    public PaginaBffDTORecive<NotificacaoResponseDTO> buscarNotificacoes(int pagina) {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/v1/notificacoes?pagina=" + pagina)
                .retrieve()
                // Novamente usamos o ParameterizedTypeReference para evitar o Type Erasure
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<NotificacaoResponseDTO>>() {});
    }

    //Marcar como Lida
    //Chamada quando o usuário clica em cima de uma notificação específica.
    public void marcarComoLida(Long idNotificacao) {
        restClient.patch() 
                .uri(apiCoreBaseUrl + "/v1/notificacoes/" + idNotificacao + "/lida")
                .retrieve()
                .toBodilessEntity(); // Retorna Void (204 No Content ou 200 OK)
    }

    //Contar Não Lidas (Badge do Sininho)
    //Essa rota deve ser extremamente rápida, pois o aplicativo pode chamá-la 
    //constantemente (polling) para saber se a "bolinha vermelha" deve aparecer.
 
    public ContagemNotificacoesDTO contarNaoLidas() {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/v1/notificacoes/nao-lidas/count")
                .retrieve()
                .body(ContagemNotificacoesDTO.class);
    }

}
