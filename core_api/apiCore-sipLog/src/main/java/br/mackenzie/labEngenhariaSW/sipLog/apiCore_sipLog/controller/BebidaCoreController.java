package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.BebidaDetalheDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoGet.FeedItemDTO.BebidaResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaBebidaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service.BebidaCoreService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/apiCore/v1/bebidas")
public class BebidaCoreController {

    private final BebidaCoreService bebidaService;

    public BebidaCoreController(BebidaCoreService bebidaService) {
        this.bebidaService = bebidaService;
    }

    //Busca para o Autocomplete do App
    @GetMapping("/buscar")
    public ResponseEntity<List<BebidaResumoDTO>> buscarBebidas(@RequestParam String q) {
        
        // Service devolve a lista de entidades
        List<Bebida> entidades = bebidaService.buscarPorNome(q);

        // Controller mapeia para DTO (Resumo leve para o dropdown do mobile)
        List<BebidaResumoDTO> dtos = entidades.stream()
                .map(b -> new BebidaResumoDTO(b.getId(), b.getNome(), b.getCategoria()))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // 2. Ver detalhes de uma bebida (Incluindo as características em JSONB)
    @GetMapping("/{id}")
    public ResponseEntity<BebidaDetalheDTO> getBebidaPorId(@PathVariable Long id) {
        
        //Busca a bebida no banco
        Bebida bebida = bebidaService.buscarPorId(id);

        //Calcula a nota média global para essa bebida
        Double notaMedia = bebidaService.obterMediaGlobal(id); // Método para calcular a nota média global

        BebidaDetalheDTO dto = new BebidaDetalheDTO(
                bebida.getId(),
                bebida.getNome(),
                bebida.getFabricante(),
                bebida.getCategoria(),
                notaMedia,
                bebida.getCaracteristicas() // O nosso querido Map<String, String> do JSONB
        );

        return ResponseEntity.ok(dto);
    }

    // 3. Criação colaborativa de nova bebida
    @PostMapping
    public ResponseEntity<BebidaResumoDTO> criarBebida(@Valid @RequestBody NovaBebidaDTO dto) {
        
        // Service salva e devolve a entidade criada com o ID gerado pelo banco
        Bebida novaBebida = bebidaService.criar(dto);

        // Retornamos o resumo para o App já poder usar esse ID na postagem do Sip
        BebidaResumoDTO responseDto = new BebidaResumoDTO(
                novaBebida.getId(), 
                novaBebida.getNome(), 
                novaBebida.getCategoria()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}