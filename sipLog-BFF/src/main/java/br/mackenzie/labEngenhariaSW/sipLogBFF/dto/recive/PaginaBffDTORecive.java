package br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive;

import java.util.List;

// Esse DTO imita o formato JSON que a Core API gera
public record PaginaBffDTORecive<T>(
    List<T> content,       // A lista de dados
    int number,            // Página atual
    int size,              // Tamanho da página
    long totalElements,    // Total de registros
    int totalPages,        // Total de páginas
    boolean first,         // É a primeira?
    boolean last           // É a última?
) {}