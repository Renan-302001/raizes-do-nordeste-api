package br.com.raizesdonordeste.api.controller.dto;

import java.time.Instant;
import java.util.List;

public class ErroResponse {

    private Instant timestamp;
    private int status;
    private String code;
    private String message;
    private String path;
    private String correlationId;
    private List<ErroCampoResponse> details;

    public ErroResponse(
            Instant timestamp,
            int status,
            String code,
            String message,
            String path,
            String correlationId,
            List<ErroCampoResponse> details
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message =message;
        this.path = path;
        this.correlationId = correlationId;
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public List<ErroCampoResponse> getDetails() {
        return details;
    }
}
