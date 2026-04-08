package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto;

import java.util.List;

public record PerfilDTO(
        UsuarioPerfilDTO usuario,
        EstatisticasDTO estatisticas,
        List<ExperienciaResumoDTO> ultimasExperiencias
) {
    public record UsuarioPerfilDTO(Long idUsuario, String nome, String bio, String fotoAvatarUrl) {}
    public record EstatisticasDTO(Integer totalDegustacoes, Double notaMediaGlobal, Integer seguindo, Integer seguidores) {}
    public record ExperienciaResumoDTO(Long idPost, String nomeBebida, Double nota, String fotoPostUrl) {}
}