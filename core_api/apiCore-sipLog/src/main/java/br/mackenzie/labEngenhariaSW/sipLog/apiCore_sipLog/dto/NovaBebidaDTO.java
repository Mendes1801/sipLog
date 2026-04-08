package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto;

import java.util.Map;

public record NovaBebidaDTO(
        String nome,
        String fabricante,
        String categoria,
        Map<String, String> caracteristicas
) {}