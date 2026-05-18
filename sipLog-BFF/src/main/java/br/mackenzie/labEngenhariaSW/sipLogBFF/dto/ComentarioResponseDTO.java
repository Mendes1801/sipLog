package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record ComentarioResponseDTO(
    Long id,
    String texto,
    String tempoDecorrido, // O BFF calculará isso: "Há 10 min", "Há 2 horas"
    UsuarioResumoDTO autor
) {}