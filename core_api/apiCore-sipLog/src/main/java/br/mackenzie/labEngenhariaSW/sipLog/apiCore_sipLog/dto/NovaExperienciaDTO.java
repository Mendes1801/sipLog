package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto;

public record NovaExperienciaDTO(
        Long idBebida,
        Double nota,
        String comentario,
        String data, // Pode usar LocalDate se preferir tipar fortemente a data
        String local
) {}