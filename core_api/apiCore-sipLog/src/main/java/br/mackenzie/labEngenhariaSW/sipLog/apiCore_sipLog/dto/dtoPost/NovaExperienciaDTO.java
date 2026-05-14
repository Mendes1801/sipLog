package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost;

public record NovaExperienciaDTO(
        Long idBebida,
        Double nota,
        String comentario,
        String fotoPostUrl,
        String local,
        String visibilidade
) {}
