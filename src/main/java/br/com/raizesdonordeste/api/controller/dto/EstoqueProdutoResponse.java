package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;

import java.time.Instant;
import java.util.UUID;

public class EstoqueProdutoResponse {

    private final UUID idEstoqueProduto;
    private final UUID idUnidade;
    private final UUID idProduto;
    private final String nomeProduto;
    private final int quantidadeDisponivel;
    private final int quantidadeReservada;
    private final int quantidadeParaVenda;
    private final int estoqueMinimo;
    private final Instant atualizadoEm;

    public EstoqueProdutoResponse(EstoqueProduto estoque) {
        this.idEstoqueProduto = estoque.getIdEstoqueProduto();
        this.idUnidade = estoque.getUnidade().getIdUnidade();
        this.idProduto = estoque.getProduto().getIdProduto();
        this.nomeProduto = estoque.getProduto().getNome();
        this.quantidadeDisponivel = estoque.getQuantidadeDisponivel();
        this.quantidadeReservada = estoque.getQuantidadeReservada();
        this.quantidadeParaVenda = estoque.getQuantidadeParaVenda();
        this.estoqueMinimo = estoque.getEstoqueMinimo();
        this.atualizadoEm = estoque.getAtualizadoEm();
    }

    public UUID getIdEstoqueProduto() {
        return idEstoqueProduto;
    }

    public UUID getIdUnidade() {
        return idUnidade;
    }

    public UUID getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public int getQuantidadeReservada() {
        return quantidadeReservada;
    }

    public int getQuantidadeParaVenda() {
        return quantidadeParaVenda;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
