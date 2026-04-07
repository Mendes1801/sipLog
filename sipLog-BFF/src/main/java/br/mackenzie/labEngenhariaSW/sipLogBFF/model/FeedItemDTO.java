package br.mackenzie.labEngenhariaSW.sipLogBFF.model; // Substitua pelo nome real do seu pacote base

public record FeedItemDTO(
        Long idPost, 
        String nomeAmigo, 
        String nomeItem, 
        Double nota, 
        String fotoUrl
) {}