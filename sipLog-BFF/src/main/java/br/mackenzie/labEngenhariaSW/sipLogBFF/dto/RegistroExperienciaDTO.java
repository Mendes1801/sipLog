package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record RegistroExperienciaDTO(
        UsuarioResumoDTO usuario,
        Long itemId, 
        Double nota, 
        String comentario, 
        String data,
        String local,
        String fotoPostUrl
) {}