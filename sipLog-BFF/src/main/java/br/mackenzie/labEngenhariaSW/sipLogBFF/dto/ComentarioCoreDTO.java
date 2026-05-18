package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

import java.time.LocalDateTime;

public record ComentarioCoreDTO(
    Long id,
    String texto,
    LocalDateTime dataCriacao, 
    UsuarioResumoDTO autor
) {}
