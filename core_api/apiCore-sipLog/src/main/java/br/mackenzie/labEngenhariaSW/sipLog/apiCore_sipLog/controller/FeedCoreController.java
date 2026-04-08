package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.FeedItemDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.FeedService;

@RestController
@RequestMapping("/internal/v1/feed")
public class FeedCoreController {

    private FeedService feedService;

    FeedCoreController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/global")
    public ResponseEntity<Page<FeedItemDTO>> getFeedGlobal(@RequestParam(defaultValue = "0") int pagina) {
        
        // Busca as entidades puras no Service
        Page<Experiencia> paginaDeEntidades = feedService.buscarFeedGlobal(pagina);

        // Transforma (mapeia) a página de Entidades para uma página de DTOs
        Page<FeedItemDTO> paginaDeDtos = paginaDeEntidades.map(exp -> {
            
            return new FeedItemDTO(
                exp.getId(),
                new FeedItemDTO.AutorDTO(
                    exp.getUsuario().getId(),
                    exp.getUsuario().getNome(),
                    exp.getUsuario().getFotoAvatarUrl()
                ),
                new FeedItemDTO.BebidaResumoDTO(
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
                )
            );
        });

        // 3. Devolve para o BFF! O Spring vai gerar um JSON lindo com a lista e os metadados da página
        return ResponseEntity.ok(paginaDeDtos);
    }

    //Retorna o feed dos amigos
    @GetMapping("/amigos")
    public ResponseEntity<Page<FeedItemDTO>> getFeedAmigos(@RequestHeader("X-User-Id") String meuId, @RequestParam int pagina) {
        
        // Busca as entidades puras no Service
        Page<Experiencia> paginaDeEntidades = feedService.buscarFeedAmigos(meuId, pagina);

        // Transforma (mapeia) a página de Entidades para uma página de DTOs
        Page<FeedItemDTO> paginaDeDtos = paginaDeEntidades.map(exp -> {
            
            return new FeedItemDTO(
                exp.getId(),
                new FeedItemDTO.AutorDTO(
                    exp.getUsuario().getId(),
                    exp.getUsuario().getNome(),
                    exp.getUsuario().getFotoAvatarUrl()
                ),
                new FeedItemDTO.BebidaResumoDTO(
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
                )
            );
        });

        // 3. Devolve para o BFF! O Spring vai gerar um JSON lindo com a lista e os metadados da página
        return ResponseEntity.ok(paginaDeDtos);
    }
}