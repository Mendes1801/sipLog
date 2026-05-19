package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ContagemNotificacoesDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NotificacaoResponseDTOApiCOre;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.NotificacaoResponseDTO;

@Service
public class NotificacaoBffService {

    @Value("${api.core.base-url}")
    private String apiCoreBaseUrl;

    private final RestClient restClient;
    private final ExperienciaBffService experienciaBffService;

    public NotificacaoBffService(RestClient restClient, ExperienciaBffService experienciaBffService) {
        this.restClient = restClient;
        this.experienciaBffService = experienciaBffService;
    }

    //Buscar Notificações (Paginado)
    //Traz o histórico de notificações do usuário logado
    public PaginaBffDTORecive<NotificacaoResponseDTO> buscarNotificacoes(int pagina) {

         PaginaBffDTORecive<NotificacaoResponseDTOApiCOre> notificacoes = restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/notificacoes?pagina=" + pagina)
                .retrieve()
                // Novamente usamos o ParameterizedTypeReference para evitar o Type Erasure
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<NotificacaoResponseDTOApiCOre>>() {});


        PaginaBffDTORecive<NotificacaoResponseDTO> notificacoesReturn = new PaginaBffDTORecive<>(
            notificacoes.content().stream().map(n -> new NotificacaoResponseDTO(
                n.id(),
                n.tipo(),
                new NotificacaoResponseDTO.UsuarioOrigemDTO(n.atorNome(), n.atorAvatarUrl()),
                // Aqui você pode montar a mensagem de acordo com o tipo e referência
                switch (n.tipo()) {
                    case "LIKE" -> "curtiu seu post.";
                    case "COMMENT" -> "comentou no seu post.";
                    case "FOLLOW" -> "começou a te seguir.";
                    default -> "fez uma ação.";
                },
                // Converter dataCriacao para um formato "HÁ X MIN"
                experienciaBffService.calcularTempoDecorrido(LocalDateTime.parse(n.dataCriacao())),
                n.lida()
            )).toList(),
            notificacoes.number(),
            notificacoes.size(),
            notificacoes.totalElements(),
            notificacoes.totalPages(),
            notificacoes.first(),
            notificacoes.last()
        );

        return notificacoesReturn;
    }

    //Marcar como Lida
    //Chamada quando o usuário clica em cima de uma notificação específica.
    public void marcarComoLida(Long idNotificacao) {
        restClient.patch() 
                .uri(apiCoreBaseUrl + "/apiCore/v1/notificacoes/" + idNotificacao + "/lida")
                .retrieve()
                .toBodilessEntity(); // Retorna Void (204 No Content ou 200 OK)
    }

    //Contar Não Lidas (Badge do Sininho)
    //Essa rota deve ser extremamente rápida, pois o aplicativo pode chamá-la 
    //constantemente (polling) para saber se a "bolinha vermelha" deve aparecer.
 
    public ContagemNotificacoesDTO contarNaoLidas() {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/notificacoes/nao-lidas/count")
                .retrieve()
                .body(ContagemNotificacoesDTO.class);
    }

}
