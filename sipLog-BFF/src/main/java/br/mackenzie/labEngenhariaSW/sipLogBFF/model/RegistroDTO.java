package br.mackenzie.labEngenhariaSW.sipLogBFF.model;

public record RegistroDTO(
        Long itemId, 
        Double nota, 
        String comentario, 
        String data
) {}