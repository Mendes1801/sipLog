package br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response;

public record RegistroExperienciaDTO(
        Long idUsuario,
        Long itemId, 
        Double nota, 
        String comentario, 
        String data,
        String local,
        String fotoPostUrl
) {}