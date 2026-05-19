package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioPerfilDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioSyncDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioUpdateDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;

@Service
public class UsuarioBffService {


    @Value("${api.core.base-url}")
    private String apiCoreBaseUrl;

    private final RestClient restClient;

    // Injetamos o RestClient que fará a ponte com a Core API
    public UsuarioBffService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * ITEM 1: Sincronização de Usuário
     * Pega os dados do JWT do Keycloak e avisa a Core API para salvar/atualizar no banco.
     */
    public void sincronizarComCoreApi(Jwt principal) {
        // 1. Extrai os dados essenciais de dentro do Token do Keycloak
        String keycloakId = principal.getSubject();
        String nome = principal.getClaimAsString("name"); // No Keycloak, o nome costuma vir na claim "name"
        String email = principal.getClaimAsString("preferred_username"); // O email costuma vir aqui

        // Se o nome vier nulo, usamos o email como fallback
        if (nome == null) {
            nome = email;
        }

        // 2. Montamos um DTO simples (Record local) para enviar na requisição
        UsuarioSyncDTO dto = new UsuarioSyncDTO(keycloakId, nome, email, null); // null para foto inicial

        // 3. Fazemos o POST para a Core API (Fire and Forget)
        restClient.post()
                .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/sync")
                .body(dto)
                .retrieve()
                .toBodilessEntity(); // Não precisamos ler o corpo da resposta, só garantir o Status 200/201
    }

    
    // ========================================================================
    // Esqueletos dos próximos métodos do Item 1 (Para o Controller não quebrar)
    // ========================================================================


    public void alternarSeguirUsuario(String meuKeycloakId, Long idAlvo) {
        // Faz o POST simples para a Core API gerenciar o Seguidor
        restClient.post()
                .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/" + idAlvo + "/seguir")
                .retrieve()
                .toBodilessEntity();
    }

    //Busca o perfil do usuário logado (Meu Perfil)
    public UsuarioPerfilDTO buscarMeuPerfil(String meuKeycloakId) {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/me")
                .retrieve()
                .body(UsuarioPerfilDTO.class);
    }


    //Busca o perfil de outro usuário, verificando se eu já o sigo
    public UsuarioPerfilDTO buscarPerfilDeTerceiro(Long idAlvo, String meuKeycloakId) {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/perfil/" + idAlvo)
                .retrieve()
                .body(UsuarioPerfilDTO.class);
    }

    public void atualizarPerfil(String keycloakId, UsuarioUpdateDTO dto) {
            restClient.put()
                    .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/me")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
    }

    public void removerPerfil(String keycloakId) {
        restClient.delete()
                .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/me")
                .retrieve()
                .toBodilessEntity();
    }

    public PaginaBffDTORecive<UsuarioResumoDTO> listagemSeguidores(Long idUsuario, int pagina) {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/usuarios/" + idUsuario + "/seguidores?pagina=" + pagina)
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<UsuarioResumoDTO>>() {});
    }
}