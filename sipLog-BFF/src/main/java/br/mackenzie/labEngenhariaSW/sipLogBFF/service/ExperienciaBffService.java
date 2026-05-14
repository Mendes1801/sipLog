package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ComentarioResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;

@Service
public class ExperienciaBffService {

    private final RestClient restClient;

    public ExperienciaBffService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void registrarNovaExperiencia(NovaExperienciaDTO dto, String keycloakId) {
        restClient.post()
                .uri("http://localhost:8082/internal/v1/experiencias")
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void alternarCurtida(Long id, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'alternarCurtida'");
    }

    public void adicionarComentario(Long id, NovoComentarioDTO dto, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'adicionarComentario'");
    }

    public PaginaBffDTORecive<ComentarioResponseDTO> buscarComentarios(Long id, int pagina) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarComentarios'");
    }

    public void editarPostagem(Long id, NovaExperienciaDTO dto, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editarPostagem'");
    }

    public void editarComentario(Long id, Long idComentario, NovoComentarioDTO dto, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editarComentario'");
    }

    public void deletarComentario(Long id, Long idComentario, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletarComentario'");
    }

    public void deletarPostagem(Long id, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletarPostagem'");
    }

}
