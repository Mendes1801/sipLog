package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ComentarioResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.RegistroExperienciaDTO;

@Service
public class ExperienciaBffService {

    private final RestClient restClient;

    public ExperienciaBffService(RestClient restClient) {
        this.restClient = restClient;
    }

    public RegistroExperienciaDTO registrarNovaExperiencia(NovaExperienciaDTO dto, String keycloakId) {
        return restClient.post()
                .uri("http://localhost:8082/internal/v1/experiencias")
                .body(dto)
                .retrieve()
                .body(RegistroExperienciaDTO.class); // BFF recebe o objeto completo da Core
    }

    public void alternarCurtida(Long idPost) {
        restClient.post()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost + "/curtir")
                .retrieve()
                .toBodilessEntity();
    }

    public ComentarioResponseDTO adicionarComentario(Long idPost, NovoComentarioDTO dto) {
        return restClient.post()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost + "/comentarios")
                .body(dto)
                .retrieve()
                .body(ComentarioResponseDTO.class);
    }

    public PaginaBffDTORecive<ComentarioResponseDTO> buscarComentarios(Long idPost, int pagina) {
        return restClient.get()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost + "/comentarios?pagina=" + pagina)
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<ComentarioResponseDTO>>() {});
    }


    public ComentarioResponseDTO editarComentario(Long idPost, Long idComentario, NovoComentarioDTO dto) {
        return restClient.put()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost + "/comentarios/" + idComentario)
                .body(dto)
                .retrieve()
                .body(ComentarioResponseDTO.class);
    }

    public void deletarComentario(Long idPost, Long idComentario) {
        restClient.delete()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost + "/comentarios/" + idComentario)
                .retrieve()
                .toBodilessEntity();
    }
    
    public void deletarPostagem(Long idPost) {
        restClient.delete()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost)
                .retrieve()
                .toBodilessEntity();
    }

    public RegistroExperienciaDTO editarExperiencia(Long idPost, NovaExperienciaDTO dto) {
        return restClient.put()
                .uri("http://localhost:8082/internal/v1/experiencias/" + idPost)
                .body(dto)
                .retrieve()
                .body(RegistroExperienciaDTO.class); // Devolve o post atualizado
    }

}
