package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.StatusUnidade;
import br.com.raizesdonordeste.api.domain.model.Unidade;

import java.util.UUID;

public class UnidadeResponse {

    private final UUID idUnidade;
    private final String codigoPublico;
    private final String nome;
    private final String cidade;
    private final String estado;
    private final StatusUnidade statusUnidade;

    public UnidadeResponse(Unidade unidade) {
        this.idUnidade = unidade.getIdUnidade();
        this.codigoPublico = unidade.getCodigoPublico();
        this.nome = unidade.getNome();
        this.cidade = unidade.getCidade();
        this.estado = unidade.getEstado();
        this.statusUnidade = unidade.getStatusUnidade();
    }

    public UUID getIdUnidade() {
        return idUnidade;
    }

    public String getCodigoPublico() {
        return codigoPublico;
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public StatusUnidade getStatusUnidade() {
        return statusUnidade;
    }
}
