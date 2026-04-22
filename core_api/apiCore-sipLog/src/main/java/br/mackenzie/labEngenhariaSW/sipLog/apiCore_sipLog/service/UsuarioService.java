package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getUsuarioPerfil(String keycloakID) {

        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakID)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return usuario;
    }
}
