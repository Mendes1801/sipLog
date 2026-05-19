package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.FeedItemDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;

@Service
public class FeedBffService {


    @Value("${api.core.base-url}")
    private String apiCoreBaseUrl;

    private final RestClient restCLient;

    public FeedBffService(RestClient restCLient) {
        this.restCLient = restCLient;
    }

    public PaginaBffDTORecive<FeedItemDTORecive> buscarFeedDeTerceiro(Long idUsuario, int pagina) {
        PaginaBffDTORecive<FeedItemDTORecive> feedDaCoreApi = restCLient.get()
                        .uri(apiCoreBaseUrl + "/apiCore/v1/feed/usuarios/" + idUsuario + "?pagina=" + pagina)
                        .retrieve()
                        .body(new ParameterizedTypeReference<PaginaBffDTORecive<FeedItemDTORecive>>() {});

        return feedDaCoreApi;
    }

    public PaginaBffDTORecive<FeedItemDTORecive> buscarFeedGlobal(int pagina) {

        // 1. O BFF pede a página para a Core API
        PaginaBffDTORecive<FeedItemDTORecive> feedApi = restCLient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/feed/global?pagina=" + pagina)
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<FeedItemDTORecive>>() {}); 

        return feedApi;
    }

    public PaginaBffDTORecive<FeedItemDTORecive> buscarFeedAmigos(int pagina) {

            PaginaBffDTORecive<FeedItemDTORecive> feedDaCoreApi = restCLient.get()
                            .uri(apiCoreBaseUrl + "/apiCore/v1/feed/amigos?pagina=" + pagina)
                            .retrieve()
                            .body(new ParameterizedTypeReference<PaginaBffDTORecive<FeedItemDTORecive>>() {});

            return feedDaCoreApi;
    }

    public PaginaBffDTORecive<FeedItemDTORecive> buscarFeedMe(int pagina) {
        PaginaBffDTORecive<FeedItemDTORecive> feedDaCoreApi = restCLient.get()
                        .uri(apiCoreBaseUrl + "/apiCore/v1/feed/me?pagina=" + pagina)
                        .retrieve()
                        .body(new ParameterizedTypeReference<PaginaBffDTORecive<FeedItemDTORecive>>() {});

        return feedDaCoreApi;
    }

}
