package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record RegistroDTO(
        Long itemId, 
        Double nota, 
        String comentario, 
        String data
) {}