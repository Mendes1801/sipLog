package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.BebidaResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.FeedItemDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.FeedCoreService;

@RestController
@RequestMapping("/apiCore/v1/feed")
public class FeedCoreController {

    private FeedCoreService feedService;

    FeedCoreController(FeedCoreService feedService) {
        this.feedService = feedService;
    }


    // Rota: /internal/v1/feed/me
    @GetMapping("/me") 
    public ResponseEntity<Page<FeedItemDTO>> getMeuFeed(
            @RequestParam(defaultValue = "0") int pagina, 
            @AuthenticationPrincipal Jwt principal) {
        
        String keycloakId = principal.getSubject();

        //Busca APENAS as entidades do usuário logado no Service
        Page<Experiencia> paginaDeEntidades = feedService.buscarMeuFeed(keycloakId, pagina);

        //Lista de IDs dos posts da página (para otimizar a busca de curtidas)
        List<Long> idsDosPostsDoFeed = paginaDeEntidades.getContent().stream()
                .map(Experiencia::getId)
                .toList();

        //Verifica no banco QUAIS desses posts o usuário já curtiu
        Set<Long> idsQueEuCurti = feedService.verificarCurtidasDoUsuario(idsDosPostsDoFeed, keycloakId);

        //Mapeia as Entidades para os DTOs do Feed
        Page<FeedItemDTO> paginaDeDtos = paginaDeEntidades.map(exp -> {
            
            // Verifica rápido na memória se o ID da experiência está no "Set"
            boolean curtidoPorMim = idsQueEuCurti.contains(exp.getId());

            return new FeedItemDTO(
                exp.getId(),
                new FeedItemDTO.AutorDTO(
                    exp.getUsuario().getId(),
                    exp.getUsuario().getNome(),
                    exp.getUsuario().getFotoAvatarUrl()
                ),
                new BebidaResumoDTO(
                    exp.getBebida().getId(),
                    exp.getBebida().getNome(),
                    exp.getBebida().getCategoria()
                ),
                new FeedItemDTO.ExperienciaFeedDTO(
                    exp.getNota(),
                    exp.getComentario(),
                    exp.getFotoPostUrl(),
                    exp.getDataCriacao().toString(),
                    exp.getLocalizacao()
                ),
                new FeedItemDTO.EngajamentoDTO(
                    curtidoPorMim,
                    exp.getTotalCurtidas() != null ? exp.getTotalCurtidas() : 0,
                    exp.getTotalComentarios() != null ? exp.getTotalComentarios() : 0
                )
            );
        });

        //Devolve o JSON estruturado e paginado!
        return ResponseEntity.ok(paginaDeDtos);
    }


    @GetMapping("/global")
    public ResponseEntity<Page<FeedItemDTO>> getFeedGlobal(@RequestParam(defaultValue = "0") int pagina, @AuthenticationPrincipal Jwt principal) {
        
        // Busca as entidades puras no Service
        Page<Experiencia> paginaDeEntidades = feedService.buscarFeedGlobal(pagina);

        //Lista de IDs dos posts que estão na página (20 posts)
        List<Long> idsDosPostsDoFeed = paginaDeEntidades.getContent().stream().map(Experiencia::getId).toList();

        //Usuario ID Keycloak
        String keycloakId = principal.getSubject();

        //Pergunta ao banco QUAIS desses 20 posts eu já curti (QUERY 2: Traz uma lista pequena de IDs)
        Set<Long> idsQueEuCurti = feedService.verificarCurtidasDoUsuario(idsDosPostsDoFeed, keycloakId);

        // Transforma (mapeia) a página de Entidades para uma página de DTOs
        Page<FeedItemDTO> paginaDeDtos = paginaDeEntidades.map(exp -> {
            
            boolean curtidoPorMim = idsQueEuCurti.contains(exp.getId());

            return new FeedItemDTO(
                exp.getId(),
                new FeedItemDTO.AutorDTO(
                    exp.getUsuario().getId(),
                    exp.getUsuario().getNome(),
                    exp.getUsuario().getFotoAvatarUrl()
                ),
                new BebidaResumoDTO(
                    exp.getBebida().getId(),
                    exp.getBebida().getNome(),
                    exp.getBebida().getCategoria()
                ),
                new FeedItemDTO.ExperienciaFeedDTO(
                    exp.getNota(),
                    exp.getComentario(),
                    exp.getFotoPostUrl(),
                    exp.getDataCriacao().toString(), // Formate a data como preferir
                    exp.getLocalizacao()
                ),

                new FeedItemDTO.EngajamentoDTO(
                    curtidoPorMim,
                    exp.getTotalCurtidas() != null ? exp.getTotalCurtidas() : 0,
                    exp.getTotalComentarios() != null ? exp.getTotalComentarios() : 0
                )
            );
        });

        //Devolve para o BFF! O Spring vai gerar um JSON  com a lista e os metadados da página
        return ResponseEntity.ok(paginaDeDtos);
    }

    //Retorna o feed dos amigos
    @GetMapping("/amigos")
    public ResponseEntity<Page<FeedItemDTO>> getFeedAmigos(@AuthenticationPrincipal Jwt principal, @RequestParam int pagina) {
        

        //Usuario ID Keycloak
        String keycloakId = principal.getSubject();
        
        // Busca as entidades puras no Service
        Page<Experiencia> paginaDeEntidades = feedService.buscarFeedAmigos(keycloakId, pagina);

        //Lista de IDs dos posts que estão na página (20 posts)
        List<Long> idsDosPostsDoFeed = paginaDeEntidades.getContent().stream().map(Experiencia::getId).toList();
        
        //Pergunta ao banco QUAIS desses 20 posts eu já curti (QUERY 2: Traz uma lista pequena de IDs)
        Set<Long> idsQueEuCurti = feedService.verificarCurtidasDoUsuario(idsDosPostsDoFeed, keycloakId);

        Page<FeedItemDTO> paginaDeDtos = paginaDeEntidades.map(exp -> {
            
            boolean curtidoPorMim = idsQueEuCurti.contains(exp.getId());

            return new FeedItemDTO(
                exp.getId(),
                new FeedItemDTO.AutorDTO(
                    exp.getUsuario().getId(),
                    exp.getUsuario().getNome(),
                    exp.getUsuario().getFotoAvatarUrl()
                ),
                new BebidaResumoDTO(
                    exp.getBebida().getId(),
                    exp.getBebida().getNome(),
                    exp.getBebida().getCategoria()
                ),
                new FeedItemDTO.ExperienciaFeedDTO(
                    exp.getNota(),
                    exp.getComentario(),
                    exp.getFotoPostUrl(),
                    exp.getDataCriacao().toString(), // Formate a data como preferir
                    exp.getLocalizacao()
                ),

                new FeedItemDTO.EngajamentoDTO(
                    curtidoPorMim,
                    exp.getTotalCurtidas() != null ? exp.getTotalCurtidas() : 0,
                    exp.getTotalComentarios() != null ? exp.getTotalComentarios() : 0
                )
            );
        });

        //Devolve para o BFF! O Spring vai gerar um JSON lindo com a lista e os metadados da página
        return ResponseEntity.ok(paginaDeDtos);
    }

    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Page<FeedItemDTO>> getFeedTerceiro(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt principal) {

        //O Service aplica a regra de privacidade e retorna as Entidades
        Page<Experiencia> postagens = feedService.buscarFeedDeTerceiro(
                principal.getSubject(), // Meu ID do Keycloak
                idUsuario,              // ID do dono da estante
                pagina
        );

        // O Controller transforma a Entidade em DTO
        // (Aqui usamos o DTO que o BFF espera, ou um similar da Core)
        Page<FeedItemDTO> dtos = postagens.map(exp -> new FeedItemDTO(
                exp.getId(),
                new FeedItemDTO.AutorDTO(
                        exp.getUsuario().getId(),
                        exp.getUsuario().getNome(),
                        exp.getUsuario().getFotoAvatarUrl()
                ),
                new BebidaResumoDTO(
                        exp.getBebida().getId(),
                        exp.getBebida().getNome(),
                        exp.getBebida().getCategoria()
                ),
                new FeedItemDTO.ExperienciaFeedDTO(
                        exp.getNota(),
                        exp.getComentario(),
                        exp.getFotoPostUrl(),
                        exp.getDataCriacao().toString(),
                        exp.getLocalizacao()
                ),
                new FeedItemDTO.EngajamentoDTO(
                        false, // Para simplificar, não verificamos se o terceiro curtiu ou não
                        exp.getTotalCurtidas() != null ? exp.getTotalCurtidas() : 0,
                        exp.getTotalComentarios() != null ? exp.getTotalComentarios() : 0
                )
        ));

        return ResponseEntity.ok(dtos);
    }
}