package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusItemCardapio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ItemCardapioResponse {

    private final UUID idItemCardapio;
    private final UUID idProduto;
    private final String nomeProduto;
    private final BigDecimal preco;
    private final StatusItemCardapio statusItem;
    private final Instant inicioVigencia;
    private final Instant fimVigencia;

    public ItemCardapioResponse(ItemCardapio item) {
        this.idItemCardapio = item.getIdItemCardapio();
        this.idProduto = item.getProduto().getIdProduto();
        this.nomeProduto = item.getProduto().getNome();
        this.preco = item.getPreco();
        this.statusItem = item.getStatusItem();
        this.inicioVigencia = item.getInicioVigencia();
        this.fimVigencia = item.getFimVigencia();
    }

    public UUID getIdItemCardapio() {
        return idItemCardapio;
    }

    public UUID getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public StatusItemCardapio getStatusItem() {
        return statusItem;
    }

    public Instant getInicioVigencia() {
        return inicioVigencia;
    }

    public Instant getFimVigencia() {
        return fimVigencia;
    }
}
