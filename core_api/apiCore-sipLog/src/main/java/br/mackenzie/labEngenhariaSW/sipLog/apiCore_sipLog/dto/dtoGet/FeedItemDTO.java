package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet;

public record FeedItemDTO(
        Long idPost,
        AutorDTO autor,
        BebidaResumoDTO bebida,
        ExperienciaFeedDTO experiencia,
        EngajamentoDTO engajamento
) {
    public record AutorDTO(Long idUsuario, String nome, String fotoAvatarUrl) {}
    public record ExperienciaFeedDTO(Double nota, String comentario, String fotoPostUrl, String data, String local) {}
    public record EngajamentoDTO(Boolean curtidoPorMim, Integer totalCurtidas, Integer totalComentarios) {}
}