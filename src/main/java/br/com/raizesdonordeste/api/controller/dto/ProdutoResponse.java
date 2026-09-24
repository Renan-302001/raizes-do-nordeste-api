package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.domain.model.StatusProduto;

import java.time.Instant;
import java.util.UUID;

public class ProdutoResponse {

    private final UUID idProduto;
    private final String codigoPublico;
    private final String nome;
    private final String descricao;
    private final String categoria;
    private final String imagemUrl;
    private final StatusProduto statusProduto;
    private final Instant criadoEm;

    public ProdutoResponse(Produto produto) {
        this.idProduto = produto.getIdProduto();
        this.codigoPublico = produto.getCodigoPublico();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.categoria = produto.getCategoria();
        this.imagemUrl = produto.getImagemUrl();
        this.statusProduto = produto.getStatusProduto();
        this.criadoEm = produto.getCriadoEm();
    }

    public UUID getIdProduto() {
        return idProduto;
    }

    public String getCodigoPublico() {
        return codigoPublico;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public StatusProduto getStatusProduto() {
        return statusProduto;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
