package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.StatusConta;

import java.time.Instant;
import java.util.UUID;

public class CadastroClienteResponse {

    private UUID idUsuario;
    private String nome;
    private String email;
    private StatusConta statusConta;
    private Instant criadoEm;

    public CadastroClienteResponse(
            UUID idUsuario,
            String nome,
            String email,
            StatusConta statusConta,
            Instant criadoEm
    ) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.statusConta = statusConta;
        this.criadoEm = criadoEm;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public StatusConta getStatusConta() {
        return statusConta;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
