package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.BebidaResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.DetalheBebidaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaBebidaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.service.BebidaBffService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/bebidas")
public class BebidaBffController {

    private final BebidaBffService bebidaService;

    public BebidaBffController(BebidaBffService bebidaService) {
        this.bebidaService = bebidaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalheBebidaDTO> buscarBebidaPorId(@PathVariable Long id) {
        DetalheBebidaDTO bebida = bebidaService.buscarPorId(id);
        
        if (bebida != null) {
            return ResponseEntity.ok(bebida);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Adcionar bebidas novas 
    @PostMapping
    public ResponseEntity<BebidaResumoDTO> adicionarBebida(@RequestBody NovaBebidaDTO novaBebida) {
        BebidaResumoDTO response = bebidaService.adicionarBebida(novaBebida);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);    
    }
    

    // ITEM 3: Buscar Bebidas para autocompletar
    @GetMapping("/buscar")
    public ResponseEntity<List<BebidaResumoDTO>> buscarBebidas(@RequestParam String q) {
        List<BebidaResumoDTO> bebidas = bebidaService.buscarNoCatalogo(q);
        return ResponseEntity.ok(bebidas);
    }

    // Adicione este endpoint na classe BebidaBffController
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = bebidaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }
}