package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MovimentacaoEstoqueRequest {

    @NotNull(message = "O tipo de movimentação é obrigatório.")
    private TipoMovimentacaoEstoque tipoMovimentacao;

    @Positive(message = "A quantidade deve ser maior que zero.")
    private int quantidade;

    @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres.")
    private String motivo;

    public TipoMovimentacaoEstoque getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacaoEstoque tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
