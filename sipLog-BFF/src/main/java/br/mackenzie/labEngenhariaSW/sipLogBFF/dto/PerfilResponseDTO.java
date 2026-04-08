package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

import java.util.List;

public record PerfilResponseDTO(
    PerfilInfoDTO perfil,
    EstatisticasDTO estatisticas,
    List<AdegaItemDTO> adegaPessoal
) {
    public record PerfilInfoDTO(String nome, String username, String fotoAvatarUrl) {}
    public record EstatisticasDTO(Integer totalSips, Double notaMedia, Integer seguidores) {}
    // O 'subtitulo' une a categoria e a principal característica (ex: "TINTO • 2022")
    public record AdegaItemDTO(Long idPost, String fotoPostUrl, String nomeBebida, String subtitulo, Double nota) {}
}