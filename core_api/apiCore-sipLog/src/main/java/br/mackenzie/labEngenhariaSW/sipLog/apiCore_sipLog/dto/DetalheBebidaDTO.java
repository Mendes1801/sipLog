package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto;

import java.util.Map;

public record DetalheBebidaDTO(
        Long idBebida,
        String nome,
        String fabricante,
        String categoria,
        Double notaMediaGlobal,
        Map<String, String> caracteristicas
) {}