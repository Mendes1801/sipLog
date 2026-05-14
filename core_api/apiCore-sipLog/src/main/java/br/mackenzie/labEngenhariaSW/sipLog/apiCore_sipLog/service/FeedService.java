package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia.Visibilidade;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.CurtidaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.SeguidorRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;

@Service
public class FeedService {
    
    final ExperienciaRepository experienciaRepository;
    final UsuarioRepository usuarioRepository;
    final CurtidaRepository curtidaRepository;
    final SeguidorRepository seguidorRepository;

    public FeedService(ExperienciaRepository experienciaRepository, SeguidorRepository seguidorRepository, UsuarioRepository usuarioRepository, CurtidaRepository curtidaRepository) {
        this.experienciaRepository = experienciaRepository;
        this.seguidorRepository = seguidorRepository;
        this.usuarioRepository = usuarioRepository;
        this.curtidaRepository = curtidaRepository;
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


    public Page<Experiencia> buscarFeedDeTerceiro(String meuKeycloakId, Long idAlvo, int pagina) {
        Usuario eu = usuarioRepository.findByKeycloakId(meuKeycloakId).orElseThrow();
        Pageable paginacao = PageRequest.of(pagina, 20);
        
        // Verifica se eu sigo esse usuário alvo
        boolean sigoEle = seguidorRepository.existsBySeguidorIdAndSeguidoId(eu.getId(), idAlvo);

        // Se eu sigo ele, posso ver as PUBLICAS e as AMIGOS. Se não sigo, só vejo as PUBLICAS.
        List<Visibilidade> visibilidades = sigoEle 
            ? List.of(Visibilidade.PUBLICA, Visibilidade.AMIGOS) 
            : List.of(Visibilidade.PUBLICA);

        return experienciaRepository.findByUsuarioIdAndVisibilidadeInOrderByDataCriacaoDesc(idAlvo, visibilidades, paginacao);
    }
}