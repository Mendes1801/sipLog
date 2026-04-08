package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.exception;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}