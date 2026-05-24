package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.BebidaResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.DetalheBebidaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaBebidaDTO;

@Service

public class BebidaBffService {

    @Value("${api.core.base-url}")
    private String apiCoreBaseUrl;

    private final RestClient restClient;

    public BebidaBffService(RestClient restClient) {
        this.restClient = restClient;
    }



    //Buscar no Catálogo (Autocomplete)
    public List<BebidaResumoDTO> buscarNoCatalogo(String q) {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/bebidas/buscar?q=" + q)
                .retrieve()
                // Como é uma Lista, usamos o ParameterizedTypeReference para o Spring saber converter o JSON corretamente
                .body(new ParameterizedTypeReference<List<BebidaResumoDTO>>() {});
    }


     //Ver Detalhes da Bebida
    public DetalheBebidaDTO buscarPorId(Long id) {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/bebidas/" + id)
                .retrieve()
                .body(DetalheBebidaDTO.class);
    }


    //3. Criação Colaborativa de Nova Bebida
    public BebidaResumoDTO adicionarBebida(NovaBebidaDTO novaBebida) {
         return restClient.post()
                .uri(apiCoreBaseUrl + "/apiCore/v1/bebidas")
                .body(novaBebida) // Envia o JSON com a nova bebida
                .retrieve()
                .body(BebidaResumoDTO.class); // Recebemos o objeto com o ID preenchido!
    }

    // Adicione este método dentro da classe BebidaBffService
    public List<String> listarCategorias() {
        return restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/bebidas/categorias")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }
        
}
