package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NovaExperienciaDTO(
    
    @NotNull(message = "O ID da bebida é obrigatório")
    Long idBebida,

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 0, message = "A nota mínima é 0")
    @Max(value = 5, message = "A nota máxima é 5")
    Double nota,

    @Size(max = 1000, message = "O comentário da experiência é muito longo")
    String comentario,

    @NotBlank(message = "A URL da foto é obrigatória")
    String fotoPostUrl,

    String localizacao
) {}