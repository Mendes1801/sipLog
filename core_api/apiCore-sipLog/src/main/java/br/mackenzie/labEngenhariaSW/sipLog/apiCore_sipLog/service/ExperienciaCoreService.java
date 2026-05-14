package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;


import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia.Visibilidade;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.AcessoNegadoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.SeguidorRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;
import jakarta.transaction.Transactional;


@Service
public class ExperienciaCoreService {
    
    private final ExperienciaRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeguidorRepository seguidorRepository;
    private final UsuarioService usuarioService;
    private final BebidaCoreService bebidaService;

    ExperienciaCoreService(UsuarioRepository usuarioRepository,SeguidorRepository seguidorRepository, ExperienciaRepository experienciaRepository, UsuarioService usuarioService, BebidaCoreService bebidaService) {
        this.usuarioRepository = usuarioRepository;
        this.experienciaRepository = experienciaRepository;
        this.seguidorRepository = seguidorRepository;
        this.usuarioService = usuarioService;
        this.bebidaService = bebidaService;
    }

    // Método para registrar uma nova experiência
    @Transactional
    public Experiencia registrarExperiencia(String keycloakId, NovaExperienciaDTO dto) {

        
        //Busca o usuário no banco usando o keycloakId
        Usuario usuario = usuarioService.getUsuarioPerfil(keycloakId);
        
        //Busca bebida no banco usando o id da bebida que veio no DTO
        Bebida bebida = bebidaService.buscarPorId(dto.idBebida());

        Experiencia experiencia = new Experiencia();

        experiencia.setUsuario(usuario);
        experiencia.setBebida(bebida);
        experiencia.setVisibilidade(Visibilidade.valueOf(dto.visibilidade().toUpperCase()));
        experiencia.setNota(dto.nota());
        experiencia.setComentario(dto.comentario());
        experiencia.setFotoPostUrl(dto.fotoPostUrl());
        experiencia.setLocalizacao(dto.local());

        return experienciaRepository.save(experiencia);
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


    @Transactional
    public Experiencia editarExperiencia(Long idPost, NovaExperienciaDTO dto, String keycloakId) {
        Experiencia experiencia = buscarPostagemValidandoAutoria(idPost, keycloakId);

        // Atualiza os campos editáveis
        experiencia.setNota(dto.nota());
        experiencia.setComentario(dto.comentario());
        experiencia.setLocalizacao(dto.local());
        experiencia.setVisibilidade(Visibilidade.valueOf(dto.visibilidade().toUpperCase()));
        // Bebida e Foto normalmente não são editáveis após o post, mas você pode habilitar se quiser.

        return experienciaRepository.save(experiencia);
    }

    @Transactional
    public void deletarExperiencia(Long idPost, String keycloakId) {
        Experiencia experiencia = buscarPostagemValidandoAutoria(idPost, keycloakId);
        experienciaRepository.delete(experiencia);
    }

// METODOS DE APOIO


     // Busca o post e GARANTE que quem está pedindo é o dono dele.
     // Impede ataques onde um usuário tenta editar/deletar o post de outro.
    public Experiencia buscarPostagemValidandoAutoria(Long idPost, String keycloakId) {
        Experiencia experiencia = experienciaRepository.findById(idPost)
                .orElseThrow(() -> new RuntimeException("Experiência não encontrada."));

        if (!experiencia.getUsuario().getKeycloakId().equals(keycloakId)) {
            throw new SecurityException("Você não tem permissão para alterar esta postagem.");
        }

        return experiencia;
    }

    public Experiencia buscarPorId(Long idPost) {
        return experienciaRepository.findById(idPost)
                .orElseThrow(() -> new RuntimeException("Experiência não encontrada."));
    }

}
