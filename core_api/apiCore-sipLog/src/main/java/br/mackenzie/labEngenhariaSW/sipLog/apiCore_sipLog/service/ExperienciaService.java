package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;


import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.AcessoNegadoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.BebidaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.SeguidorRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;


@Service
public class ExperienciaService {
    
    private final ExperienciaRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BebidaRepository bebidaRepository;
    private final SeguidorRepository seguidorRepository;

    ExperienciaService(UsuarioRepository usuarioRepository,SeguidorRepository seguidorRepository, BebidaRepository bebidaRepository, ExperienciaRepository experienciaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.bebidaRepository = bebidaRepository;
        this.experienciaRepository = experienciaRepository;
        this.seguidorRepository = seguidorRepository;
    }

    // Método para registrar uma nova experiência
    public Experiencia registrarExperiencia(String keycloakId, NovaExperienciaDTO dto) {

        //Busca o usuário no banco usando o keycloakId
        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Experiencia experiencia = new Experiencia();
        experiencia.setUsuario(usuario);

        Bebida bebida = bebidaRepository.findById(dto.idBebida())
                .orElseThrow(() -> new RuntimeException("Bebida não encontrada"));

        experiencia.setBebida(bebida);
        experiencia.setNota(dto.nota());
        experiencia.setComentario(dto.comentario());

        return experienciaRepository.save(experiencia); // retornar a experiência criada (ou algum DTO de resposta)
    }


    // Método para buscar os detalhes de uma experiência específica, verifica se o usuario tem acesso a essa experiência (se é dele, se é publica ou de um amigo) e retorna a entidade experiencia
    public Experiencia getExperiencia(String idExperiencia, String keycloakId) {
        Long id = Long.parseLong(idExperiencia);

        //Procura a experiencia no banco, se não encontrar lança exceção
        Experiencia exp = experienciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experiência não encontrada"));


        //Pega o usuario que está fazendo a requisição
        Usuario usuarioRequisitante = usuarioRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        //Pega o autor da experiencia
        Usuario autorExperiencia = exp.getUsuario();
                
        //Verifica a experiencia solicitada pertence ao usuario requisitante
        if(!exp.getUsuario().getKeycloakId().equals(keycloakId)) {
            throw new AcessoNegadoException("Acesso negado à experiência");
        }

        //Verificar se o usuario é seguidor do autor da experiencia
        if(!seguidorRepository.existsBySeguidorAndSeguido(usuarioRequisitante, autorExperiencia)) {
            throw new AcessoNegadoException("Acesso negado à experiência");
        }
        
        return exp;
    }

}
