package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet;

public record RegistroExperienciaDTO(
        Long idUsuario,
        Long itemId, 
        Double nota, 
        String comentario, 
        String data,
        String local,
        String fotoPostUrl
) {}