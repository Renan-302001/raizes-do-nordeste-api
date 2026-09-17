package br.com.raizesdonordeste.api.controller.dto;

public class ErroCampoResponse {

    private String field;
    private String message;

    public ErroCampoResponse(
            String field,
            String message
    ) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
