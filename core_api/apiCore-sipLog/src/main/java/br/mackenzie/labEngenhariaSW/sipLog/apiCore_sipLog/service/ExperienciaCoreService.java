package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Comentario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Curtida;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia.Visibilidade;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.TipoNotificacao;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.AcessoNegadoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.RecursoNaoEncontradoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ComentarioRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.CurtidaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.SeguidorRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;
import jakarta.transaction.Transactional;


@Service
public class ExperienciaCoreService {
    
    private final ExperienciaRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeguidorRepository seguidorRepository;
    private final UsuarioCoreService usuarioService;
    private final BebidaCoreService bebidaService;
    private final CurtidaRepository curtidaRepository;
    private final ComentarioRepository comentarioRepository;
    private final NotificacaoCoreService notificacaoCoreService;

    ExperienciaCoreService(UsuarioRepository usuarioRepository, NotificacaoCoreService notificacaoCoreService,CurtidaRepository curtidaRepository, ComentarioRepository comentarioRepository, SeguidorRepository seguidorRepository, ExperienciaRepository experienciaRepository, UsuarioCoreService usuarioService, BebidaCoreService bebidaService) {
        this.usuarioRepository = usuarioRepository;
        this.experienciaRepository = experienciaRepository;
        this.seguidorRepository = seguidorRepository;
        this.usuarioService = usuarioService;
        this.bebidaService = bebidaService;
        this.curtidaRepository = curtidaRepository;
        this.comentarioRepository = comentarioRepository;
        this.notificacaoCoreService = notificacaoCoreService;
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

    @Transactional
    public void alternarCurtida(Long idPost, String keycloakId) {
        Usuario eu = usuarioService.getUsuarioPerfil(keycloakId);
        Experiencia post = buscarPorId(idPost);

        // Verifica se a curtida já existe
        Optional<Curtida> curtidaExistente = curtidaRepository.findByUsuarioIdAndExperienciaId(eu.getId(), post.getId());

        if (curtidaExistente.isPresent()) {
            // Se já curtiu, o "toggle" significa descurtir (Deletar)
            curtidaRepository.delete(curtidaExistente.get());
        } else {
            // Se não curtiu, cria a nova curtida
            Curtida novaCurtida = new Curtida();
            novaCurtida.setUsuario(eu);
            novaCurtida.setExperiencia(post);
            curtidaRepository.save(novaCurtida);
            
            // Disparar evento para gerar Notificação para o dono do post
            // Depois verificar se o descurtir também gera uma noficação
            notificacaoCoreService.gerarNotificacao(eu, post.getUsuario(), TipoNotificacao.CURTIDA, post.getId());
        }
    }

    @Transactional
    public Comentario adicionarComentario(Long idPost, NovoComentarioDTO dto, String keycloakId) {
        Usuario meuUsuario = usuarioService.getUsuarioPerfil(keycloakId);
        Experiencia post = buscarPorId(idPost);

        Comentario comentario = new Comentario();

        comentario.setTexto(dto.texto());
        comentario.setUsuario(meuUsuario);
        comentario.setExperiencia(post);
        // A data de criação será preenchida automaticamente pelo @PrePersist da entidade

        // Cria a notifica no repository Notificacao
        notificacaoCoreService.gerarNotificacao(meuUsuario, post.getUsuario(), TipoNotificacao.COMENTARIO, post.getId());

        return comentarioRepository.save(comentario);
    }

    public Page<Comentario> listarComentarios(Long idPost, int pagina) {
        // Traz os comentários do mais antigo para o mais novo (Padrão de UI do Instagram/Threads)
        Pageable paginacao = PageRequest.of(pagina, 30); 
        return comentarioRepository.findByExperienciaIdOrderByDataCriacaoAsc(idPost, paginacao);
    }

     //Edita um comentário validando a autoria e o vínculo com a experiência.
    @Transactional
    public Comentario editarComentario(Long idPost, Long idComentario, NovoComentarioDTO dto, String keycloakId) {
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comentário não encontrado."));

        // Proteção: Garante que o comentário pertence à postagem indicada na URL
        if (!comentario.getExperiencia().getId().equals(idPost)) {
            throw new IllegalArgumentException("O comentário não pertence a esta experiência.");
        }

        // Proteção: Garante que apenas o autor do comentário pode editá-lo
        if (!comentario.getUsuario().getKeycloakId().equals(keycloakId)) {
            throw new AcessoNegadoException("Você não tem permissão para editar este comentário.");
        }

        comentario.setTexto(dto.texto());
        return comentarioRepository.save(comentario);
    }


     //Deleta um comentário validando a autoria.
    @Transactional
    public void deletarComentario(Long idPost, Long idComentario, String keycloakId) {
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comentário não encontrado."));

        if (!comentario.getExperiencia().getId().equals(idPost)) {
            throw new IllegalArgumentException("O comentário não pertence a esta experiência.");
        }

        if (!comentario.getUsuario().getKeycloakId().equals(keycloakId)) {
            throw new AcessoNegadoException("Você não tem permissão para deletar este comentário.");
        }

        comentarioRepository.delete(comentario);
    }


}
