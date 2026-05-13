package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ComentarioResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;

@Service
public class ExperienciaBffService {

    public void criarPostagem(NovaExperienciaDTO dto, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'criarPostagem'");
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

}
