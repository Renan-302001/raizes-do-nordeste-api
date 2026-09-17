package br.com.raizesdonordeste.api.application.exception;

public class SenhaInvalidaException extends RuntimeException{

    public SenhaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
