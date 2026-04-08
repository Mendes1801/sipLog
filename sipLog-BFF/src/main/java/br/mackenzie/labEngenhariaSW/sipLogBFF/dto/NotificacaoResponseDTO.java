package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

public record NotificacaoResponseDTO(
    Long idNotificacao,
    String tipo, // Ex: "LIKE", "FOLLOW", "COMMENT"
    UsuarioOrigemDTO usuarioOrigem,
    String mensagem,
    String tempoAtras, // Ex: "HÁ 40 MIN"
    Boolean lida
) {
    public record UsuarioOrigemDTO(String nome, String fotoAvatarUrl) {}
}