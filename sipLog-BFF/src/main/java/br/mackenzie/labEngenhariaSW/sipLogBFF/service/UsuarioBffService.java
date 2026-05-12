package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.security.oauth2.jwt.Jwt;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioPerfilDTO;

public class UsuarioBffService {

    public void sincronizarComCoreApi(Jwt principal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sincronizarComCoreApi'");
    }

    public UsuarioPerfilDTO buscarMeuPerfil(String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarMeuPerfil'");
    }

    public UsuarioPerfilDTO buscarPerfilDeTerceiro(Long idUsuario, String subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarPerfilDeTerceiro'");
    }

    public void alternarSeguirUsuario(String subject, Long idAlvo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'alternarSeguirUsuario'");
    }

}
