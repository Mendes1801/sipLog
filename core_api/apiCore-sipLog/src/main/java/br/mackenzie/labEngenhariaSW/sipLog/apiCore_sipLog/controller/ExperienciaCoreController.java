package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.RegistroExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoReturn.ReturnNovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.ExperienciaService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/apiCore/v1/experiencia")
public class ExperienciaCoreController {
    
    private final ExperienciaService experienciaService;

    ExperienciaCoreController(ExperienciaService experienciaService) {
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
    ResponseEntity<ReturnNovaExperienciaDTO> registrarExperiencia(@RequestBody NovaExperienciaDTO experienciaDTO, @RequestHeader("X-User-Keycloak-Id") String keycloakId) {

        //Chama o service para registrar nova postagem
        //Service retorna a entidade criada
        Experiencia expEntity = experienciaService.registrarExperiencia(keycloakId, experienciaDTO);
        
        //Cria um DTO especifico para retornar ao BFF somente o necessário
        ReturnNovaExperienciaDTO retorno = new ReturnNovaExperienciaDTO(expEntity.getId(), expEntity.getDataCriacao());

        //Retorna o 201 CREATE e o DTO de retorno
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);

    }


}
