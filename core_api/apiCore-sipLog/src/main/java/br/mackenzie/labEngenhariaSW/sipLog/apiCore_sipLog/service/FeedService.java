package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.CurtidaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;

@Service
public class FeedService {
    
    final ExperienciaRepository experienciaRepository;
    final UsuarioRepository usuarioRepository;
    final CurtidaRepository curtidaRepository;

    public FeedService(ExperienciaRepository experienciaRepository, UsuarioRepository usuarioRepository, CurtidaRepository curtidaRepository) {
        this.experienciaRepository = experienciaRepository;
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
        return experienciaRepository.findAllByOrderByDataCriacaoDesc(paginacao);

    }

    public Page<Experiencia> buscarFeedAmigos(String meuKeycloakId, int pagina) {

        //Pega o usuario pelo ID do Keycloak
        Usuario usuario = usuarioRepository.findByKeycloakId(meuKeycloakId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Define a paginação (20 itens por página)
        Pageable paginacao = PageRequest.of(pagina, 20); 

        //Retorna as experiências onde o ID do autor esteja dentro da lista de pessoas que o meu usuário segue
        return experienciaRepository.findFeedAmigos(usuario.getId(), paginacao);
    }
}