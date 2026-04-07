package br.mackenzie.labEngenhariaSW.sipLogBFF.model;
import java.util.Map;

public record CharacterDTO(
    String name,
    String image,
    String mainColor,
    Map<String, String> powerstats
) {
    
}
