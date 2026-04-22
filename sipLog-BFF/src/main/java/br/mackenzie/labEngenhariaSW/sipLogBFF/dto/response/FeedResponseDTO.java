package br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response;

public record FeedResponseDTO(
    Long idPost,

    //Usuario
    Long idUsuario,
    String nomeAutor,
    String fotoAvatarUrl,

    //Experiencia
    String tempoDecorrido, // "Há 2h" ou Long horasAtras se o flutter for calcular
    String local,
    String fotoPostUrl,

    //Bebida
    Long idBebida,
    String nomeBebida,
    String categoriaBebida,
    Double nota,
    String comentario,

    // Engajamento:
    boolean curtidoPorMim,
    int totalCurtidas,
    int totalComentarios
) {}