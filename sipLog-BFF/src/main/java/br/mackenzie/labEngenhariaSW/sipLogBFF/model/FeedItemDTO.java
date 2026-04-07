package br.mackenzie.labEngenhariaSW.sipLogBFF.model;

public record FeedItemDTO(
        Long idPost,
        AutorDTO autor,
        BebidaResumoDTO bebida,
        ExperienciaFeedDTO experiencia
) {
    public record AutorDTO(Long idUsuario, String nome, String fotoAvatarUrl) {}
    public record BebidaResumoDTO(Long idBebida, String nome, String categoria) {}
    public record ExperienciaFeedDTO(Double nota, String comentario, String fotoPostUrl, String data, String local) {}
}