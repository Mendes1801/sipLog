package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record UsuarioPerfilDTO(
    Long id,
    String nome,
    String bio,
    String fotoAvatarUrl,
    Long totalSips,        // Contagem de experiências postadas
    Long totalSeguidores,  // Quantas pessoas o seguem
    Long totalSeguindo,    // Quantas pessoas ele segue
    Boolean seguidoPorMim  // True se o usuário logado segue este perfil (null se for o meu próprio)
) {}