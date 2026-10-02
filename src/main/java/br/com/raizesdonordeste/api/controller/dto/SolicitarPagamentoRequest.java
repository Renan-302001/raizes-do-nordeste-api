package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.application.gateway.CenarioPagamentoMock;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class SolicitarPagamentoRequest {

    @NotNull(message = "O resultado simulado é obrigatório.")
    @Schema(example = "APROVADO", allowableValues = {"APROVADO", "RECUSADO", "ERRO"})
    private CenarioPagamentoMock resultadoSimulado;

    public CenarioPagamentoMock getResultadoSimulado() {
        return resultadoSimulado;
    }

    public void setResultadoSimulado(CenarioPagamentoMock resultadoSimulado) {
        this.resultadoSimulado = resultadoSimulado;
    }
}
