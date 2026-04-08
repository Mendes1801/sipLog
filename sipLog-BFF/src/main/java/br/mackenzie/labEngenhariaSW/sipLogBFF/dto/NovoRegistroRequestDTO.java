package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record NovoRegistroRequestDTO(
        NovaBebidaDTO novaBebida,
        Long idBebida,
        Double nota,
        String comentario,
        String data, // Pode usar LocalDate se preferir tipar fortemente a data
        String local
) {}