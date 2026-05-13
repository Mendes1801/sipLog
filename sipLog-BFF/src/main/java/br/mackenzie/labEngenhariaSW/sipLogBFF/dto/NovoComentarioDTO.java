package br.mackenzie.labEngenhariaSW.sipLogBFF.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NovoComentarioDTO(
    @NotBlank(message = "O texto do comentário não pode ser vazio")
    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    String texto
) {}