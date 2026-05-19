package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.ComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.RegistroExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.UsuarioResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Comentario;
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
@RequestMapping("/apiCore/v1/experiencias")
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
    public ResponseEntity<RegistroExperienciaDTO> registrarExperiencia(
                @RequestBody NovaExperienciaDTO experienciaDTO, 
                @AuthenticationPrincipal Jwt principal) {

        //Chama o service para registrar nova postagem
        Experiencia entidade = experienciaService.registrarExperiencia(principal.getSubject(), experienciaDTO);

        // Mapeia para o DTO de resposta
        RegistroExperienciaDTO response = new RegistroExperienciaDTO(
            entidade.getUsuario().getId(),
            entidade.getBebida().getId(),
            entidade.getNota(),
            entidade.getComentario(),
            entidade.getDataCriacao().toString(),
            entidade.getLocalizacao(),
            entidade.getFotoPostUrl()
        );

        //Retorna o 201 CREATE e o DTO de retorno
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Editar o Sip (O usuário pode querer mudar a nota ou corrigir o texto)
    @PutMapping("/{id}")
    public ResponseEntity<RegistroExperienciaDTO> editarExperiencia(
            @PathVariable Long id, 
            @Valid @RequestBody NovaExperienciaDTO dto, 
            @AuthenticationPrincipal Jwt principal) {
        
        Experiencia entidade = experienciaService.editarExperiencia(id, dto, principal.getSubject());
        
        RegistroExperienciaDTO response = new RegistroExperienciaDTO(
            entidade.getUsuario().getId(),
            entidade.getBebida().getId(),
            entidade.getNota(),
            entidade.getComentario(),
            entidade.getDataCriacao().toString(),
            entidade.getLocalizacao(),
            entidade.getFotoPostUrl()
        );
                
        return ResponseEntity.ok(response);
    }


     //Curtir ou descurtir um Sip (Se já tiver curtido, descurte. Se não tiver curtido, curta)
    @PostMapping("/{id}/curtir")
    public ResponseEntity<Void> alternarCurtida(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt principal) {
        
        experienciaService.alternarCurtida(id, principal.getSubject());
        return ResponseEntity.ok().build();
    }

    //Adicionar um comentario em uma experiencia
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioDTO> adicionarComentario(
            @PathVariable Long id,
            @RequestBody NovoComentarioDTO comentario,
            @AuthenticationPrincipal Jwt principal) {
        
        Comentario comentarioEntity = experienciaService.adicionarComentario(id, comentario, principal.getSubject());
        
        ComentarioDTO response = new ComentarioDTO(
        comentarioEntity.getId(),
        comentarioEntity.getTexto(),
        LocalDateTime.now(),
        new UsuarioResumoDTO(
            comentarioEntity.getUsuario().getId(),
            comentarioEntity.getUsuario().getNome(),
            comentarioEntity.getUsuario().getFotoAvatarUrl()
        ));
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    //Listar Comentários
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<Page<ComentarioDTO>> listarComentarios(
            @PathVariable Long id, 
            @RequestParam(defaultValue = "0") int pagina) {
        
        // Service devolve as Entidades
        Page<Comentario> entidades = experienciaService.listarComentarios(id, pagina);

        // Controller mapeia para o DTO esperado
        Page<ComentarioDTO> dtos = entidades.map(comentarioEntity -> new ComentarioDTO(
            comentarioEntity.getId(),
            comentarioEntity.getTexto(),
            comentarioEntity.getDataCriacao(),
            new UsuarioResumoDTO(
                comentarioEntity.getUsuario().getId(),
                comentarioEntity.getUsuario().getNome(),
                comentarioEntity.getUsuario().getFotoAvatarUrl()
            )
        ));

        return ResponseEntity.ok(dtos);
    }

    //Deletar o Sip
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExperiencia(
            @PathVariable Long id, 
            @AuthenticationPrincipal Jwt principal) {
        
        experienciaService.deletarExperiencia(id, principal.getSubject());
        return ResponseEntity.noContent().build();
    }


    //Editar um comentário existente
    @PutMapping("/{id}/comentarios/{idComentario}")
    public ResponseEntity<ComentarioDTO> editarComentario(
            @PathVariable Long id,
            @PathVariable Long idComentario,
            @Valid @RequestBody NovoComentarioDTO dto,
            @AuthenticationPrincipal Jwt principal) {

        // Service executa a regra e retorna a entidade atualizada
        Comentario c = experienciaService.editarComentario(id, idComentario, dto, principal.getSubject());

        // Controller faz o De/Para para o DTO de resposta
        ComentarioDTO response = new ComentarioDTO(
            c.getId(),
            c.getTexto(),
            c.getDataCriacao(),
            new UsuarioResumoDTO(
                c.getUsuario().getId(),
                c.getUsuario().getNome(),
                c.getUsuario().getFotoAvatarUrl()
            )
        );

        return ResponseEntity.ok(response);
    }

    //Deletar um comentário
    @DeleteMapping("/{id}/comentarios/{idComentario}")
    public ResponseEntity<Void> deletarComentario(
            @PathVariable Long id,
            @PathVariable Long idComentario,
            @AuthenticationPrincipal Jwt principal) {

        experienciaService.deletarComentario(id, idComentario, principal.getSubject());
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
