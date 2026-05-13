package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.FeedItemDTORecive.BebidaResumoDTO;
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

    // Buscar bebidas por id 
    @GetMapping("/{id}")
    public ResponseEntity<BebidaResumoDTO> buscarBebidaPorId(@RequestParam Long id) {
        BebidaResumoDTO bebida = bebidaService.buscarPorId(id);
        
        
        //Verificar se essa é a melhor forma de fazer isso
        if (bebida != null) {
            return ResponseEntity.ok(bebida);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Adcionar bebidas novas 
    @PostMapping
    public ResponseEntity<BebidaResumoDTO> adicionarBebida(@RequestBody BebidaResumoDTO novaBebida) {
        BebidaResumoDTO bebidaAdicionada = bebidaService.adicionarBebida(novaBebida);
        return ResponseEntity.status(201).body(bebidaAdicionada);
    }
    

    // ITEM 3: Buscar Bebidas para autocompletar
    @GetMapping("/buscar")
    public ResponseEntity<List<BebidaResumoDTO>> buscarBebidas(@RequestParam String q) {
        List<BebidaResumoDTO> bebidas = bebidaService.buscarNoCatalogo(q);
        return ResponseEntity.ok(bebidas);
    }
}