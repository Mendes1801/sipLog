package br.mackenzie.labEngenhariaSW.sipLogBFF.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 1. Captura os erros que a API-CORE devolveu (4xx).
     * Exemplo: Regras de negócio, usuário não encontrado, sem permissão.
     * O RestClient do BFF lança HttpClientErrorException. Nós pegamos a mensagem original da Core API e repassamos!
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleCoreApiExceptions(HttpClientErrorException ex) {
        // Pega o Status HTTP exato que a API-CORE mandou (ex: 400, 403, 404)
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        
        // Pega o corpo do erro original (JSON) que a API-CORE enviou
        String corpoDoErroOriginal = ex.getResponseBodyAsString();

        return ResponseEntity.status(status != null ? status : HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(corpoDoErroOriginal);
    }

    /*
     * 2. Captura os erros de Servidor da API-CORE (5xx).
     * Exemplo: Banco de dados da API-CORE caiu.
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleCoreApiServerExceptions(HttpServerErrorException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", ex.getStatusCode().value());
        erro.put("erro", "Erro interno na Core API.");
        erro.put("mensagem", "O serviço principal está indisponível no momento.");

        return ResponseEntity.status(ex.getStatusCode()).body(erro);
    }

    /*
     * 3. Captura os erros de validação (@Valid) dos DTOs do próprio BFF.
     * Exemplo: Flutter enviou um Comentário com texto vazio ou sem idBebida.
     * Nós validamos isso no BFF para nem gastar banda da Core API.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> detalhesDosErros = new HashMap<>();
        
        // Mapeia qual campo falhou e a mensagem (ex: {"texto": "O comentário não pode ser vazio"})
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            detalhesDosErros.put(erro.getField(), erro.getDefaultMessage());
        }

        Map<String, Object> respostaErro = new HashMap<>();
        respostaErro.put("timestamp", LocalDateTime.now());
        respostaErro.put("status", HttpStatus.BAD_REQUEST.value());
        respostaErro.put("erro", "Erro de Validação de Dados");
        respostaErro.put("detalhes", detalhesDosErros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respostaErro);
    }

    /*
     * 4. Fallback: Qualquer outro erro não previsto no BFF.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erro.put("erro", "Erro inesperado no BFF");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}