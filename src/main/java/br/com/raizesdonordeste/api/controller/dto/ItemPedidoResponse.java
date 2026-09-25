package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.ItemPedido;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemPedidoResponse {

    private final UUID idItemPedido;
    private final UUID idProduto;
    private final String nomeProduto;
    private final int quantidade;
    private final BigDecimal precoUnitario;
    private final BigDecimal valorDescontoItem;
    private final BigDecimal valorTotalItem;

    public ItemPedidoResponse(ItemPedido item) {
        this.idItemPedido = item.getIdItemPedido();
        this.idProduto = item.getProduto().getIdProduto();
        this.nomeProduto = item.getNomeProduto();
        this.quantidade = item.getQuantidade();
        this.precoUnitario = item.getPrecoUnitario();
        this.valorDescontoItem = item.getValorDescontoItem();
        this.valorTotalItem = item.getValorTotalItem();
    }

    public UUID getIdItemPedido() {
        return idItemPedido;
    }

    public UUID getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getValorDescontoItem() {
        return valorDescontoItem;
    }

    public BigDecimal getValorTotalItem() {
        return valorTotalItem;
    }
}
