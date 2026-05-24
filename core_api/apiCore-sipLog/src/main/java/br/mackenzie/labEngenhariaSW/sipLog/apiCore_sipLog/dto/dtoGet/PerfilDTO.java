package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet;

public record PerfilDTO(
        Boolean seguidoPorMim,
        UsuarioPerfilDTO usuario,
        EstatisticasDTO estatisticas
) {
    public record UsuarioPerfilDTO(Long idUsuario, String nome, String bio, String fotoAvatarUrl) {}
    public record EstatisticasDTO(Integer totalDegustacoes, Double notaMediaGlobal, Integer seguindo, Integer seguidores) {}
}