package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.UsuarioSyncDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPut.UsuarioUpdateDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Seguidor;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.TipoNotificacao;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.SeguidorRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;

@Service
public class UsuarioCoreService {

    private final UsuarioRepository usuarioRepository;
    private final SeguidorRepository seguidorRepository;
    private final NotificacaoCoreService notificacaoCoreService;

    UsuarioCoreService(UsuarioRepository usuarioRepository, NotificacaoCoreService notificacaoCoreService, SeguidorRepository seguidorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.seguidorRepository = seguidorRepository;
        this.notificacaoCoreService = notificacaoCoreService;
    }

    public Usuario getUsuarioPerfil(String keycloakID) {
        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakID)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return usuario;
    }

    //Sincronizar
    //Verifica se o usuário existe. Se não existir, cria. Se existir, apenas atualiza o nome (caso tenha mudado no Keycloak).
    @Transactional
    public Usuario sincronizar(UsuarioSyncDTO dto) {
        return usuarioRepository.findByKeycloakId(dto.keycloakId())
                .map(usuarioExistente -> {
                    // Usuário já logou antes. Atualizamos apenas dados que vêm do provedor de identidade.
                    usuarioExistente.setNome(dto.nome());
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseGet(() -> {
                    // Primeiro login da pessoa no aplicativo!
                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setKeycloakId(dto.keycloakId());
                    novoUsuario.setNome(dto.nome());
                    novoUsuario.setEmail(dto.email());
                    novoUsuario.setFotoAvatarUrl(dto.fotoAvatarUrl());
                    return usuarioRepository.save(novoUsuario);
                });
    }

    //Atualizar perfil
    //Atualiza dados inseridos manualmente pelo usuário no aplicativo.
    @Transactional
    public Usuario atualizarPerfil(String keycloakId, UsuarioUpdateDTO dto) {
        Usuario usuario = getUsuarioPerfil(keycloakId);

        // Atualiza apenas os campos que o DTO permite
        if (dto.nome() != null) usuario.setNome(dto.nome());
        if (dto.bio() != null) usuario.setBio(dto.bio());
        if (dto.fotoAvatarUrl() != null) usuario.setFotoAvatarUrl(dto.fotoAvatarUrl());

        return usuarioRepository.save(usuario);
    }

    //Deletar conta
    //Exclui o usuário do banco de dados local
    @Transactional
    public void deletarConta(String keycloakId) {
        Usuario usuario = getUsuarioPerfil(keycloakId);
        
        // O Hibernate precisa estar configurado com CascadeType.REMOVE 
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void alternarSeguir(String meuKeycloakId, Long idAlvo) {
        Usuario eu = getUsuarioPerfil(meuKeycloakId);
        
        // Regra de Negócio: Não posso seguir a mim mesmo!
        if (eu.getId().equals(idAlvo)) {
            throw new IllegalArgumentException("Você não pode seguir a si mesmo.");
        }

        Usuario alvo = usuarioRepository.findById(idAlvo)
                .orElseThrow(() -> new RuntimeException("Usuário alvo não encontrado"));

        // Verifica se a relação já existe na tabela de ligação
        Optional<Seguidor> relacaoExistente = seguidorRepository.findBySeguidorIdAndSeguidoId(eu.getId(), alvo.getId());

        if (relacaoExistente.isPresent()) {
            // Se achou, significa que já sigo. Então "dar unfollow" é apagar o registro.
            seguidorRepository.delete(relacaoExistente.get());
        } else {
            // Se não achou, significa que não sigo. Criamos a relação.
            Seguidor novoSeguidor = new Seguidor();
            novoSeguidor.setSeguidor(eu);
            novoSeguidor.setSeguido(alvo);
            seguidorRepository.save(novoSeguidor);
            
            //cria o "Seguidor novoSeguidor"
            notificacaoCoreService.gerarNotificacao(eu, alvo, TipoNotificacao.NOVO_SEGUIDOR, null);
        }
    }

    public Page<Usuario> listarSeguidores(Long idUsuario, int pagina) {
        Pageable paginacao = PageRequest.of(pagina, 20); // 20 seguidores por página
        
        // Retorna a Entidade! O seu repositório fará o JOIN necessário
        // para trazer os Usuários que seguem este idUsuario.
        return seguidorRepository.findBySeguidoId(idUsuario, paginacao); 
    }

}
