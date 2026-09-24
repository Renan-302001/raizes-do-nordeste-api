package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.TipoMovimentacaoEstoque;

import java.time.Instant;
import java.util.UUID;

public class MovimentacaoEstoqueResponse {

    private final UUID idMovimentacao;
    private final TipoMovimentacaoEstoque tipoMovimentacao;
    private final int quantidade;
    private final int saldoDisponivelAnterior;
    private final int saldoDisponivelPosterior;
    private final String motivo;
    private final Instant criadoEm;

    public MovimentacaoEstoqueResponse(MovimentacaoEstoque movimentacao) {
        this.idMovimentacao = movimentacao.getIdMovimentacaoEstoque();
        this.tipoMovimentacao = movimentacao.getTipoMovimentacao();
        this.quantidade = movimentacao.getQuantidade();
        this.saldoDisponivelAnterior = movimentacao.getSaldoDisponivelAnterior();
        this.saldoDisponivelPosterior = movimentacao.getSaldoDisponivelPosterior();
        this.motivo = movimentacao.getMotivo();
        this.criadoEm = movimentacao.getCriadoEm();
    }

    public UUID getIdMovimentacao() {
        return idMovimentacao;
    }

    public TipoMovimentacaoEstoque getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getSaldoDisponivelAnterior() {
        return saldoDisponivelAnterior;
    }

    public int getSaldoDisponivelPosterior() {
        return saldoDisponivelPosterior;
    }

    public String getMotivo() {
        return motivo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
