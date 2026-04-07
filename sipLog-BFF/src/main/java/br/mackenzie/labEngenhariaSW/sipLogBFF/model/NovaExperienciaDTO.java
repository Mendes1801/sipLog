package br.mackenzie.labEngenhariaSW.sipLogBFF.model;

public record NovaExperienciaDTO(
        Long idBebida,
        Double nota,
        String comentario,
        String data, // Pode usar LocalDate se preferir tipar fortemente a data
        String local
) {}