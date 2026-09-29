package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.application.gateway.CenarioPagamentoMock;
import jakarta.validation.constraints.NotNull;

public class SolicitarPagamentoRequest {

    @NotNull(message = "O resultado simulado é obrigatório.")
    private CenarioPagamentoMock resultadoSimulado;

    public CenarioPagamentoMock getResultadoSimulado() {
        return resultadoSimulado;
    }

    public void setResultadoSimulado(CenarioPagamentoMock resultadoSimulado) {
        this.resultadoSimulado = resultadoSimulado;
    }
}
