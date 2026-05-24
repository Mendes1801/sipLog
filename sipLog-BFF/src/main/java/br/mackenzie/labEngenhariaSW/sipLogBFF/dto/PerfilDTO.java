package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record PerfilDTO(
        Boolean seguindoPorMim,
        UsuarioPerfilDTO usuario,
        EstatisticasDTO estatisticas
) {
    public record UsuarioPerfilDTO(Long idUsuario, String nome, String bio, String fotoAvatarUrl) {}
    public record EstatisticasDTO(Integer totalDegustacoes, Double notaMediaGlobal, Integer seguindo, Integer seguidores) {}
}