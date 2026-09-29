package br.com.raizesdonordeste.api.application.exception;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String message) {
        super(message);
    }
}
