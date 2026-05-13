package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record ComentarioResponseDTO(
    Long id,
    String texto,
    String tempoDecorrido, // O BFF calculará isso: "Há 10 min", "Há 2 horas"
    AutorComentarioDTO autor
) {
    // Record aninhado para organizar a resposta estruturada para o Mobile
    public record AutorComentarioDTO(
        Long id,
        String nome,
        String fotoAvatarUrl
    ) {}
}