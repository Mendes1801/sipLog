package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ComentarioCoreDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.ComentarioResponseDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovaExperienciaDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.NovoComentarioDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.UsuarioResumoDTO;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.recive.PaginaBffDTORecive;
import br.mackenzie.labEngenhariaSW.sipLogBFF.dto.response.RegistroExperienciaDTO;

@Service
public class ExperienciaBffService {

    @Value("${api.core.base-url}")
    private String apiCoreBaseUrl;

    private final RestClient restClient;

    public ExperienciaBffService(RestClient restClient) {
        this.restClient = restClient;
    }
    public RegistroExperienciaDTO registrarNovaExperiencia(NovaExperienciaDTO dto, String keycloakId) {
        return restClient.post()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias")
                .body(dto)
                .retrieve()
                .body(RegistroExperienciaDTO.class); // BFF recebe o objeto completo da Core
    }

    public void alternarCurtida(Long idPost) {
        restClient.post()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost + "/curtir")
                .retrieve()
                .toBodilessEntity();
    }

     //Adicionar Comentário
     //Consome o dado bruto da Core e traduz em tempo decorrido para o Flutter.
    public ComentarioResponseDTO adicionarComentario(Long idPost, NovoComentarioDTO dto) {
        ComentarioCoreDTO coreResponse = restClient.post()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost + "/comentarios")
                .body(dto)
                .retrieve()
                .body(ComentarioCoreDTO.class);

        return mapearParaResponseDTO(coreResponse);
    }

    //3. Buscar Comentários (Listagem Paginada)
    //Faz o mapeamento em lote de todas as datas da página antes de mandar para o Mobile.
    public PaginaBffDTORecive<ComentarioResponseDTO> buscarComentarios(Long idPost, int pagina) {
        PaginaBffDTORecive<ComentarioCoreDTO> paginaCore = restClient.get()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost + "/comentarios?pagina=" + pagina)
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaBffDTORecive<ComentarioCoreDTO>>() {});

        // Converte a lista de itens da página aplicando o cálculo de tempo
        List<ComentarioResponseDTO> conteudoFormatado = paginaCore.content().stream()
                .map(this::mapearParaResponseDTO)
                .toList();

        // Devolve a nova estrutura paginada com os DTOs corretos do BFF
        return new PaginaBffDTORecive<>(
                conteudoFormatado,
                paginaCore.number(),
                paginaCore.size(),
                paginaCore.totalElements(),
                paginaCore.totalPages(),
                paginaCore.first(),
                paginaCore.last()
        );
    }


    //Editar Comentário
    //Agora mapeia o retorno da Core calculando o "Há tanto tempo".
    public ComentarioResponseDTO editarComentario(Long idPost, Long idComentario, NovoComentarioDTO dto) {
        ComentarioCoreDTO coreResponse = restClient.put()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost + "/comentarios/" + idComentario)
                .body(dto)
                .retrieve()
                .body(ComentarioCoreDTO.class);

        return mapearParaResponseDTO(coreResponse);
    }

    public void deletarComentario(Long idPost, Long idComentario) {
        restClient.delete()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost + "/comentarios/" + idComentario)
                .retrieve()
                .toBodilessEntity();
    }
    
    public void deletarPostagem(Long idPost) {
        restClient.delete()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost)
                .retrieve()
                .toBodilessEntity();
    }

    public RegistroExperienciaDTO editarExperiencia(Long idPost, NovaExperienciaDTO dto) {
        return restClient.put()
                .uri(apiCoreBaseUrl + "/apiCore/v1/experiencias/" + idPost)
                .body(dto)
                .retrieve()
                .body(RegistroExperienciaDTO.class); // Devolve o post atualizado
    }


//    // ===================================== MÉTODOS AUXILIARES ======================================

    //Converte o DTO bruto da Core no DTO formatado do BFF.
    private ComentarioResponseDTO mapearParaResponseDTO(ComentarioCoreDTO coreDto) {
        String tempoDecorrido = calcularTempoDecorrido(coreDto.dataCriacao());
        
        return new ComentarioResponseDTO(
                coreDto.id(),
                coreDto.texto(),
                tempoDecorrido, // String calculada: "Há 5 min", "Há 2 dias"
                new UsuarioResumoDTO(
                        coreDto.autor().id(),
                        coreDto.autor().nome(),
                        coreDto.autor().fotoAvatarUrl()
                )
        );
    }

    //Algoritmo de cálculo de tempo relativo (Padrão redes sociais)
    private String calcularTempoDecorrido(LocalDateTime dataCriacao) {
        if (dataCriacao == null) return "Agora";

        LocalDateTime agora = LocalDateTime.now();
        Duration duracao = Duration.between(dataCriacao, agora);

        long segundos = duracao.getSeconds();
        long minutos = duracao.toMinutes();
        long horas = duracao.toHours();
        long dias = duracao.toDays();

        if (segundos < 60) {
            return "Agora";
        } else if (minutos < 60) {
            return "Há " + minutos + (minutos == 1 ? " min" : " min");
        } else if (horas < 24) {
            return "Há " + horas + (horas == 1 ? " hora" : " horas");
        } else if (dias < 7) {
            return "Há " + dias + (dias == 1 ? " dia" : " dias");
        } else {
            // Se passar de uma semana, exibe a contagem de semanas ou a data fixa curta (ex: "2 sem")
            long semanas = dias / 7;
            return "Há " + semanas + (semanas == 1 ? " semana" : " semanas");
        }
    }

}
