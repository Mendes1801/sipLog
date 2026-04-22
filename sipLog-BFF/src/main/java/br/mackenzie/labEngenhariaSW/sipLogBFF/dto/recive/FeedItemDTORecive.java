package br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.EngajamentoDTO;

public record FeedItemDTORecive(
        Long idPost,
        AutorDTO autor,
        BebidaResumoDTO bebida,
        ExperienciaFeedDTO experiencia,
        EngajamentoDTO engajamento
) {
    public record AutorDTO(Long idUsuario, String nome, String fotoAvatarUrl) {}
    public record BebidaResumoDTO(Long idBebida, String nome, String categoria) {}
    public record ExperienciaFeedDTO(Double nota, String comentario, String fotoPostUrl, String data, String local) {}
}