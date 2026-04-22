package br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response;

import java.util.List;

public record ExploreResponseDTO(
    List<TendenciaDTO> tendencias,
    List<SugestaoAmizadeDTO> sugestoesAmizade
) {
    public record TendenciaDTO(Long idBebida, String nome, String tag, String fotoUrl) {}
    public record SugestaoAmizadeDTO(Long idUsuario, String nome, String username, String fotoAvatarUrl, Boolean seguindo) {}
}