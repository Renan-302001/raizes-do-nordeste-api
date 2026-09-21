package br.com.raizesdonordeste.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pagamento")
    private UUID idPagamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "chave_idempotencia", nullable = false, unique = true, length = 100)
    private String chaveIdempotencia;

    @Column(name = "id_transacao_gateway", unique = true, length = 100)
    private String idTransacaoGateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false, length = 30)
    private MetodoPagamento metodoPagamento = MetodoPagamento.MOCK;

    @Column(name = "valor_solicitado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorSolicitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 20)
    private StatusPagamento statusPagamento = StatusPagamento.SOLICITADO;

    @Column(name = "codigo_retorno", length = 50)
    private String codigoRetorno;

    @Column(name = "mensagem_retorno", length = 255)
    private String mensagemRetorno;

    @Column(name = "solicitado_em", nullable = false)
    private Instant solicitadoEm;

    @Column(name = "processado_em")
    private Instant processadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Pagamento() {
    }

    public Pagamento(
            Pedido pedido,
            String chaveIdempotencia,
            BigDecimal valorSolicitado
    ) {
        if (pedido == null || chaveIdempotencia == null || chaveIdempotencia.isBlank()) {
            throw new IllegalArgumentException("Pedido e chave de idempotência são obrigatórios.");
        }

        if (valorSolicitado == null || valorSolicitado.signum() <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero.");
        }

        this.pedido = pedido;
        this.chaveIdempotencia = chaveIdempotencia;
        this.valorSolicitado = valorSolicitado;
    }

    @PrePersist
    protected void aoCriar() {
        Instant agora = Instant.now();
        this.solicitadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    public void aprovar(
            String idTransacaoGateway,
            String codigoRetorno,
            String mensagemRetorno
    ) {
        registrarResultado(
                StatusPagamento.APROVADO,
                idTransacaoGateway,
                codigoRetorno,
                mensagemRetorno
        );
    }

    public void recusar(
            String idTransacaoGateway,
            String codigoRetorno,
            String mensagemRetorno
    ) {
        registrarResultado(
                StatusPagamento.RECUSADO,
                idTransacaoGateway,
                codigoRetorno,
                mensagemRetorno
        );
    }

    public void registrarErro(String codigoRetorno, String mensagemRetorno) {
        registrarResultado(
                StatusPagamento.ERRO,
                null,
                codigoRetorno,
                mensagemRetorno
        );
    }

    private void registrarResultado(
            StatusPagamento novoStatus,
            String idTransacaoGateway,
            String codigoRetorno,
            String mensagemRetorno
    ) {
        if (statusPagamento != StatusPagamento.SOLICITADO) {
            throw new IllegalStateException("O pagamento já foi processado.");
        }

        this.statusPagamento = novoStatus;
        this.idTransacaoGateway = idTransacaoGateway;
        this.codigoRetorno = codigoRetorno;
        this.mensagemRetorno = mensagemRetorno;
        this.processadoEm = Instant.now();
    }

    public UUID getIdPagamento() {
        return idPagamento;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public String getChaveIdempotencia() {
        return chaveIdempotencia;
    }

    public String getIdTransacaoGateway() {
        return idTransacaoGateway;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public String getCodigoRetorno() {
        return codigoRetorno;
    }

    public String getMensagemRetorno() {
        return mensagemRetorno;
    }

    public Instant getSolicitadoEm() {
        return solicitadoEm;
    }

    public Instant getProcessadoEm() {
        return processadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
