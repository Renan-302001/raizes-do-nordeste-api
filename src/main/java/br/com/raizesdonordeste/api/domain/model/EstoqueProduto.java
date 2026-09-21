package br.com.raizesdonordeste.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estoque_produto")
public class EstoqueProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_estoque_produto")
    private UUID idEstoqueProduto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "quantidade_disponivel", nullable = false)
    private int quantidadeDisponivel;

    @Column(name = "quantidade_reservada", nullable = false)
    private int quantidadeReservada;

    @Column(name = "estoque_minimo", nullable = false)
    private int estoqueMinimo;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected EstoqueProduto() {
    }

    public EstoqueProduto(
            Unidade unidade,
            Produto produto,
            int quantidadeDisponivel,
            int estoqueMinimo
    ) {
        if (quantidadeDisponivel < 0 || estoqueMinimo < 0) {
            throw new IllegalArgumentException("As quantidades do estoque não podem ser negativas.");
        }

        this.unidade = unidade;
        this.produto = produto;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.estoqueMinimo = estoqueMinimo;
    }

    @PrePersist
    @PreUpdate
    protected void aoSalvar() {
        this.atualizadoEm = Instant.now();
    }

    public int getQuantidadeParaVenda() {
        return quantidadeDisponivel - quantidadeReservada;
    }

    public boolean possuiDisponibilidade(int quantidade) {
        return quantidade > 0 && getQuantidadeParaVenda() >= quantidade;
    }

    public void adicionar(int quantidade) {
        validarQuantidadePositiva(quantidade);
        this.quantidadeDisponivel += quantidade;
    }

    public void reservar(int quantidade) {
        validarQuantidadePositiva(quantidade);

        if (!possuiDisponibilidade(quantidade)) {
            throw new IllegalStateException("Estoque insuficiente para reservar o produto.");
        }

        this.quantidadeReservada += quantidade;
    }

    public void liberarReserva(int quantidade) {
        validarQuantidadePositiva(quantidade);

        if (quantidade > quantidadeReservada) {
            throw new IllegalStateException("A quantidade supera o estoque reservado.");
        }

        this.quantidadeReservada -= quantidade;
    }

    public void confirmarSaida(int quantidade) {
        validarQuantidadePositiva(quantidade);

        if (quantidade > quantidadeReservada) {
            throw new IllegalStateException("A quantidade supera o estoque reservado.");
        }

        this.quantidadeReservada -= quantidade;
        this.quantidadeDisponivel -= quantidade;
    }

    private void validarQuantidadePositiva(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }

    public UUID getIdEstoqueProduto() {
        return idEstoqueProduto;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public int getQuantidadeReservada() {
        return quantidadeReservada;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
