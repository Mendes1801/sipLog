package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.dto.dtoPost.NovaBebidaDTO;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.BebidaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository.ExperienciaRepository;

@Service
public class BebidaCoreService {


    private final BebidaRepository bebidaRepository;
    private final ExperienciaRepository experienciaRepository;

    public BebidaCoreService(BebidaRepository bebidaRepository, ExperienciaRepository experienciaRepository) {
        this.bebidaRepository = bebidaRepository;
        this.experienciaRepository = experienciaRepository;
    }

    public List<Bebida> buscarPorNome(String query) {
        // Retorna top 10 resultados para não travar o celular com listas gigantes
        return bebidaRepository.findTop10ByNomeContainingIgnoreCase(query);
    }

    public Bebida buscarPorId(Long id) {
        return bebidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bebida não encontrada no catálogo."));
    }


    //Calcula a média global de uma bebida, ou seja, a média de todas as avaliações que os usuários deram para ela
    public Double obterMediaGlobal(Long idBebida) {
        Double media = experienciaRepository.calcularMediaGlobalDaBebida(idBebida);
        
        // Se ninguém avaliou ainda, o banco retorna null. Garantimos o 0.0
        return media != null ? media : 0.0;
    }

    @Transactional
    public Bebida criar(NovaBebidaDTO dto) {
        Bebida bebida = new Bebida();
        bebida.setNome(dto.nome());
        bebida.setCategoria(dto.categoria());
        bebida.setFabricante(dto.fabricante());
        bebida.setCaracteristicas(dto.caracteristicas()); // Salva o dicionário JSONB direto no banco!
        
        return bebidaRepository.save(bebida);
    }

}
