package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.RegistroExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.ExperienciaCoreService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/apiCore/v1/experiencia")
public class ExperienciaCoreController {
    
    private final ExperienciaCoreService experienciaService;

    ExperienciaCoreController(ExperienciaCoreService experienciaService) {
        this.experienciaService = experienciaService;
    }


    //Retorna os detalhes de uma experiencia especifica
    @GetMapping("/{id}")
    public ResponseEntity<RegistroExperienciaDTO> getExperiencia(@RequestParam String idExperiencia, @RequestHeader("X-User-Keycloak-Id") String keycloakId) {
        
        //Busca a experiência no banco usando o service
        Experiencia experiencia = experienciaService.getExperiencia(idExperiencia, keycloakId);

        // Cria o DTO de resposta a partir da entidade
        RegistroExperienciaDTO registroExperienciaDTO = new RegistroExperienciaDTO(
            experiencia.getUsuario().getId(),
            experiencia.getBebida().getId(),
            experiencia.getNota(),
            experiencia.getComentario(),
            experiencia.getDataCriacao().toString(),
            experiencia.getLocalizacao(),
            experiencia.getFotoPostUrl()
        );
        
        return ResponseEntity.ok(registroExperienciaDTO);
    }
    
    //Cria um novo registro de experiência (postagem) e retorna o id e data de criação da experiência criada
    @PostMapping
    public ResponseEntity<Void> registrarExperiencia(
                @RequestBody NovaExperienciaDTO experienciaDTO, 
                @AuthenticationPrincipal Jwt principal) {

        //Chama o service para registrar nova postagem
        experienciaService.registrarExperiencia(principal.getSubject(), experienciaDTO);

        //Retorna o 201 CREATE e o DTO de retorno
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    //Editar o Sip (O usuário pode querer mudar a nota ou corrigir o texto)
    @PutMapping("/{id}")
    public ResponseEntity<Void> editarExperiencia(
            @PathVariable Long id, 
            @Valid @RequestBody NovaExperienciaDTO dto, 
            @AuthenticationPrincipal Jwt principal) {
        
        experienciaService.editarExperiencia(id, dto, principal.getSubject());
        return ResponseEntity.ok().build();
    }

    //Deletar o Sip
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExperiencia(
            @PathVariable Long id, 
            @AuthenticationPrincipal Jwt principal) {
        
        experienciaService.deletarExperiencia(id, principal.getSubject());
        return ResponseEntity.noContent().build();
    }

}
