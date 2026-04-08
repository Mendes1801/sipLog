package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto;

public record RegistroDTO(
        Long idUsuario,
        Long itemId, 
        Double nota, 
        String comentario, 
        String data
) {}