package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.UsuarioResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.PerfilDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.PerfilDTO.UsuarioPerfilDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.UsuarioSyncDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPut.UsuarioUpdateDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.UsuarioCoreService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/apiCore/v1/usuarios")
public class UsuariosCoreController {
    

    private final UsuarioCoreService usuarioService;

    UsuariosCoreController(UsuarioCoreService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //Busca meu perfil
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> getMyUser(@AuthenticationPrincipal Jwt principal) {

        String keycloakID = principal.getId();
        Usuario usuarioPerfil = usuarioService.getUsuarioPerfil(keycloakID);

        UsuarioPerfilDTO usuarioPerfilDTO = new UsuarioPerfilDTO(
            usuarioPerfil.getId(),
            usuarioPerfil.getNome(),
            usuarioPerfil.getBio(),
            usuarioPerfil.getFotoAvatarUrl()
        );

        return ResponseEntity.ok(usuarioPerfilDTO);
    }

    // Atualizar Meu Perfil
    @PutMapping("/me")
    public ResponseEntity<PerfilDTO> atualizarMeuPerfil(
            @AuthenticationPrincipal Jwt principal, 
            @RequestBody UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioService.atualizarPerfil(principal.getSubject(), dto);

        PerfilDTO perfilDTO = new PerfilDTO(
            new UsuarioPerfilDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getBio(),
                usuario.getFotoAvatarUrl()
            ),
            null, // Estatísticas omitidas para simplificação
            null  // Últimas experiências podem ser preenchidas posteriormente
            );

        return ResponseEntity.ok(perfilDTO);
    }

    //Deletar (Perfil) Conta
    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarConta(@AuthenticationPrincipal Jwt principal) {
        usuarioService.deletarConta(principal.getSubject());
        return ResponseEntity.noContent().build();
    }

    //Sincronização vinda do BFF
    @PostMapping("/sync")
    public ResponseEntity<Void> sincronizarUsuario(@RequestBody UsuarioSyncDTO dto) {
        usuarioService.sincronizar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //Buscar Perfil de Terceiros
    @GetMapping("/perfil/{idAlvo}")
    public ResponseEntity<UsuarioPerfilDTO> getPerfilDeTerceiro(
            @PathVariable Long idAlvo, 
            @AuthenticationPrincipal Jwt principal) {

        Usuario usuario = usuarioService.getUsuarioPerfil(principal.getSubject());
        UsuarioPerfilDTO perfilDTO = new UsuarioPerfilDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getBio(),
            usuario.getFotoAvatarUrl()
        );
        return ResponseEntity.ok(perfilDTO);
    }

    //Seguir ou Deixar de Seguir
    @PostMapping("/{idAlvo}/seguir")
    public ResponseEntity<Void> alternarSeguir(
            @PathVariable Long idAlvo, 
            @AuthenticationPrincipal Jwt principal) {
        usuarioService.alternarSeguir(principal.getSubject(), idAlvo);
        return ResponseEntity.ok().build();
    }

    //Listar Seguidores de alguém (Público, mas requer token válido)
    @GetMapping("/{idUsuario}/seguidores")
    public ResponseEntity<Page<UsuarioResumoDTO>> getSeguidores(
            @PathVariable Long idUsuario, 
            @RequestParam(defaultValue = "0") int pagina) {
        
        // 1. O Service busca no banco e devolve uma Página de Entidades puras
        Page<Usuario> paginaDeEntidades = usuarioService.listarSeguidores(idUsuario, pagina);
        
        // 2. O Controller faz o "De/Para" (Parse) de Entidade para DTO usando o .map()
        Page<UsuarioResumoDTO> paginaDeDtos = paginaDeEntidades.map(usuario -> 
            new UsuarioResumoDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getFotoAvatarUrl()
            )
        );

        // 3. Devolvemos os DTOs limpos para o BFF
        return ResponseEntity.ok(paginaDeDtos);
    }
}
