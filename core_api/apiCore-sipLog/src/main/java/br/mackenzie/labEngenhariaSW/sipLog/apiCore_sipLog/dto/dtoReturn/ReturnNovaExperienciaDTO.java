package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoReturn;

import java.time.LocalDateTime;

public record ReturnNovaExperienciaDTO(
        Long idGerado,
        LocalDateTime dataCriacao
) {}