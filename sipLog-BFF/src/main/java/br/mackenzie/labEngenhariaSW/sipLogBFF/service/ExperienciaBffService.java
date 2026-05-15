package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

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

    public RegistroExperienciaDTO editarExperiencia(Long id, NovaExperienciaDTO dto) {
        return restClient.put()
                .uri("http://localhost:8082/internal/v1/experiencias/" + id)
                .body(dto)
                .retrieve()
                .body(RegistroExperienciaDTO.class); // Devolve o post atualizado
    }

}
