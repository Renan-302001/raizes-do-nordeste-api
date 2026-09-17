package br.com.raizesdonordeste.api.application.exception;

public class CadastroDuplicadoException extends RuntimeException{

    public CadastroDuplicadoException(String mensagem) {
        super (mensagem);
    }

}
