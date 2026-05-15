package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia.Visibilidade;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.CurtidaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.SeguidorRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;

@Service
public class FeedCoreService {
    
    final ExperienciaRepository experienciaRepository;
    final UsuarioRepository usuarioRepository;
    final CurtidaRepository curtidaRepository;
    final SeguidorRepository seguidorRepository;
    final UsuarioCoreService usuarioService;

    public FeedCoreService(ExperienciaRepository experienciaRepository, SeguidorRepository seguidorRepository, UsuarioRepository usuarioRepository, CurtidaRepository curtidaRepository, UsuarioCoreService usuarioService) {
        this.experienciaRepository = experienciaRepository;
        this.seguidorRepository = seguidorRepository;
        this.usuarioRepository = usuarioRepository;
        this.curtidaRepository = curtidaRepository;
        this.usuarioService = usuarioService;
    }

    public Page<Experiencia> buscarMeuFeed(String keycloakId, int pagina) {
        
        //Pega o usuário logado
        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        //Define a paginação (20 itens por página)
        Pageable paginacao = PageRequest.of(pagina, 20); 

        //Traz apenas as experiências que pertencem a ele
        return experienciaRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId(), paginacao);
    }

    public Set<Long> verificarCurtidasDoUsuario(List<Long> experienciasId, String KeycloakId) {

        if (experienciasId == null || experienciasId.isEmpty()) {
            return Collections.emptySet(); 
        }

        Usuario usuario = usuarioRepository.findByKeycloakId(KeycloakId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return curtidaRepository.findExperienciasCurtidasPeloUsuario(usuario.getId(), experienciasId);
    }

    public Page<Experiencia> buscarFeedGlobal(int pagina) {
        Pageable paginacao = PageRequest.of(pagina, 20); 
        
        // Feed Global SÓ pode mostrar postagens PÚBLICAS
        return experienciaRepository.findByVisibilidadeOrderByDataCriacaoDesc(Visibilidade.PUBLICA, paginacao);
    }

    public Page<Experiencia> buscarFeedAmigos(String meuKeycloakId, int pagina) {
        Usuario usuario = usuarioRepository.findByKeycloakId(meuKeycloakId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable paginacao = PageRequest.of(pagina, 20); 

        // Feed de Amigos mostra PUBLICA e AMIGOS (Bloqueia as PRIVADAS dos meus amigos)
        List<Visibilidade> visibilidadesPermitidas = List.of(Visibilidade.PUBLICA, Visibilidade.AMIGOS);

        return experienciaRepository.findFeedAmigos(usuario.getId(), visibilidadesPermitidas, paginacao);
    }


    @Transactional(readOnly = true)
    public Page<Experiencia> buscarFeedDeTerceiro(String meuKeycloakId, Long idAlvo, int pagina) {
        // Busca quem é o usuário que está fazendo a requisição
        Usuario eu = usuarioService.getUsuarioPerfil(meuKeycloakId);
        Pageable paginacao = PageRequest.of(pagina, 20);
        
        // Regra de Negócio: Eu sigo essa pessoa?
        boolean sigoEle = seguidorRepository.existsBySeguidorIdAndSeguidoId(eu.getId(), idAlvo);

        // Define a lista de visibilidades permitidas
        List<Visibilidade> visibilidadesPermitidas;
        
        if (eu.getId().equals(idAlvo)) {
            // Se eu estou visitando minha própria estante por essa rota: vejo TUDO
            visibilidadesPermitidas = List.of(Visibilidade.PUBLICA, Visibilidade.AMIGOS, Visibilidade.PRIVADA);
        } else if (sigoEle) {
            // Se eu sigo ele: vejo o que é Público e o que é para Amigos
            visibilidadesPermitidas = List.of(Visibilidade.PUBLICA, Visibilidade.AMIGOS);
        } else {
            // Se sou um estranho: vejo apenas o que é Público
            visibilidadesPermitidas = List.of(Visibilidade.PUBLICA);
        }

        // Chama o Repository que criamos no passo anterior
        return experienciaRepository.findByUsuarioIdAndVisibilidadeInOrderByDataCriacaoDesc(
                idAlvo, 
                visibilidadesPermitidas, 
                paginacao
        );
    }
}