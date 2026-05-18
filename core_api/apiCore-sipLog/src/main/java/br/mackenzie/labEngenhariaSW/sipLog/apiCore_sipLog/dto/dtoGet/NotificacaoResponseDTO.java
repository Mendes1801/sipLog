package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet;

public record NotificacaoResponseDTO(
    Long id, // ID da notificação (pode ser usado para marcar como lida)
    String atorNome,
    String atorAvatarUrl,
    String tipo,
    Long referenciaId, // ID do post curtido/comentado ou do novo seguidor. O BFF pode usar isso para montar a URL de destino.
    boolean lida,
    String dataCriacao
) {

}
