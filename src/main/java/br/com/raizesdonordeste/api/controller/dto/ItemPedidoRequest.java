package br.com.raizesdonordeste.api.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public class ItemPedidoRequest {

    @NotNull(message = "O produto é obrigatório.")
    private UUID idProduto;

    @Positive(message = "A quantidade deve ser maior que zero.")
    private int quantidade;

    public UUID getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(UUID idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
