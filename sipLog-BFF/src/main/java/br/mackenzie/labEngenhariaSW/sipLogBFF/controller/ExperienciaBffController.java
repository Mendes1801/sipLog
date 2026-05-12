package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ComentarioResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.service.ExperienciaBffService;

@RestController
@RequestMapping("/api/v1/experiencias")
public class ExperienciaBffController {

    private final ExperienciaBffService experienciaService;

    public ExperienciaBffController(ExperienciaBffService experienciaService) {
        this.experienciaService = experienciaService;
    }

    // ITEM 3: Criar Experiência (O "Postar" final)
    @PostMapping
    public ResponseEntity<Void> criarSip(@RequestBody NovaExperienciaDTO dto, @AuthenticationPrincipal Jwt principal) {
        experienciaService.criarPostagem(dto, principal.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ITEM 5: Curtir / Descurtir
    @PostMapping("/{id}/curtir")
    public ResponseEntity<Void> alternarCurtida(@PathVariable Long id, @AuthenticationPrincipal Jwt principal) {
        experienciaService.alternarCurtida(id, principal.getSubject());
        return ResponseEntity.ok().build();
    }

    // ITEM 5: Comentar
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<Void> adicionarComentario(
            @PathVariable Long id, 
            @RequestBody NovoComentarioDTO dto, 
            @AuthenticationPrincipal Jwt principal) {
        
        experienciaService.adicionarComentario(id, dto, principal.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ITEM 5: Listar Comentários de um Post
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<PaginaBffDTORecive<ComentarioResponseDTO>> listarComentarios(
            @PathVariable Long id, 
            @RequestParam(defaultValue = "0") int pagina) {
        
        PaginaBffDTORecive<ComentarioResponseDTO> comentarios = experienciaService.buscarComentarios(id, pagina);
        return ResponseEntity.ok(comentarios);
    }
}