package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.FeedItemDTORecive.BebidaResumoDTO;

@Service
public class BebidaBffService {

    public List<BebidaResumoDTO> buscarNoCatalogo(String q){

        return null;
    }

    public BebidaResumoDTO buscarPorId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }

    public BebidaResumoDTO adicionarBebida(BebidaResumoDTO novaBebida) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'adicionarBebida'");
    }
    
}
