package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record FeedResponseDTO(
    Long idPost,
    AutorDTO autor,
    String tempoAtras, // Ex: "HÁ 3 HORAS" (Calculado pelo BFF)
    String localizacao,
    String fotoPostUrl,
    BebidaDTO bebida,
    ExperienciaDTO experiencia,
    EngajamentoDTO engajamento
) {
    public record AutorDTO(Long idUsuario, String nome, String fotoAvatarUrl) {}
    public record BebidaDTO(Long idBebida, String nome) {}
    public record ExperienciaDTO(Double nota, String comentario) {}
    public record EngajamentoDTO(Boolean curtidoPorMim, Integer totalCurtidas, Integer totalComentarios) {}
}