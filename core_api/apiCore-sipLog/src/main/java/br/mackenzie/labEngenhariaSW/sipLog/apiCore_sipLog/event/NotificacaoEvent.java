package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.event;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.TipoNotificacao;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;

public record NotificacaoEvent(
    Usuario ator, 
    Usuario recebedor, 
    TipoNotificacao tipo, 
    Long referenciaId
) {}