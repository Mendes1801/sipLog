package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet;

import java.time.LocalDateTime;

public record ComentarioDTO(
        Long id, 
        String texto, 
        LocalDateTime dataCriacao, 
        UsuarioResumoDTO autor
)  {}
