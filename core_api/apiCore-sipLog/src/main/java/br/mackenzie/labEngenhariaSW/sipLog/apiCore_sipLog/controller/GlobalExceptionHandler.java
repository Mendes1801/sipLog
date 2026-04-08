package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.controller;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.AcessoNegadoException;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Quando o Service lançar AcessoNegadoException, este método é chamado
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Object> handleAcessoNegado(AcessoNegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", ex.getMessage()));
    }

    // Quando o Service lançar RecursoNaoEncontradoException, este método é chamado
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }
}