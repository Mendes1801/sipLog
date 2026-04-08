package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

import java.util.Map;

public record NovaBebidaDTO(
        String nome,
        String fabricante,
        String categoria,
        Map<String, String> caracteristicas
) {}