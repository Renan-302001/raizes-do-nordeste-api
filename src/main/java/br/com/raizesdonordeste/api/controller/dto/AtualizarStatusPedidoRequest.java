package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AtualizarStatusPedidoRequest {

    @NotNull(message = "O novo status é obrigatório.")
    @Schema(example = "EM_PREPARO")
    private StatusPedido novoStatus;

    @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres.")
    @Schema(example = "Pedido encaminhado para a cozinha.")
    private String observacao;

    public StatusPedido getNovoStatus() { return novoStatus; }
    public void setNovoStatus(StatusPedido novoStatus) { this.novoStatus = novoStatus; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
