package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.PerfilDTO.UsuarioPerfilDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/apiCore/v1/usuarios")
public class UsuariosCoreController {
    

    private final UsuarioService usuarioService;

    UsuariosCoreController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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
    
}
