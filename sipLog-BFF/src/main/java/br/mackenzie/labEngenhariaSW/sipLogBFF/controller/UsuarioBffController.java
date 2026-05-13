package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioPerfilDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioUpdateDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.service.UsuarioBffService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioBffController {

    private final UsuarioBffService usuarioService;

    public UsuarioBffController(UsuarioBffService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ITEM 1: Sincronizar usuário após o login no Keycloak
    @PostMapping("/sync")
    public ResponseEntity<Void> sincronizarUsuario(@AuthenticationPrincipal Jwt principal) {
        // O Service vai extrair os dados do JWT e avisar a Core API
        usuarioService.sincronizarComCoreApi(principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ITEM 1: Buscar o próprio perfil (Minha Estante)
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> getMeuPerfil(@AuthenticationPrincipal Jwt principal) {
        UsuarioPerfilDTO perfil = usuarioService.buscarMeuPerfil(principal.getSubject());
        return ResponseEntity.ok(perfil);
    }

    //  Atualizar perfil do usuário logado
    @PutMapping("/me")
    public ResponseEntity<Void> atualizarMeuPerfil(
            @AuthenticationPrincipal Jwt principal, 
            @RequestBody UsuarioUpdateDTO dto) {
        
        usuarioService.atualizarPerfil(principal.getSubject(), dto);
        return ResponseEntity.ok().build();
    }

    // Deletar conta do usuário logado
    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarMinhaConta(@AuthenticationPrincipal Jwt principal) {
        usuarioService.removerPerfil(principal.getSubject());
        return ResponseEntity.noContent().build();
    }

    // Listar seguidores de um usuário específico (Paginado)
    @GetMapping("/{idUsuario}/seguidores")
    public ResponseEntity<PaginaBffDTORecive<UsuarioResumoDTO>> getSeguidores(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int pagina) {
        
        PaginaBffDTORecive<UsuarioResumoDTO> seguidores = usuarioService.listagemSeguidores(idUsuario, pagina);
        return ResponseEntity.ok(seguidores);
    }


    // ITEM 1: Buscar o perfil de um amigo
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioPerfilDTO> getPerfilUsuario(@PathVariable Long idUsuario, @AuthenticationPrincipal Jwt principal) {
        UsuarioPerfilDTO perfil = usuarioService.buscarPerfilDeTerceiro(idUsuario, principal.getSubject());
        return ResponseEntity.ok(perfil);
    }

    // ITEM 4: Seguir / Deixar de Seguir (Rede Social)
    @PostMapping("/{idAlvo}/seguir")
    public ResponseEntity<Void> alternarSeguir(@PathVariable Long idAlvo, @AuthenticationPrincipal Jwt principal) {
        usuarioService.alternarSeguirUsuario(principal.getSubject(), idAlvo);
        return ResponseEntity.ok().build();
    }
}