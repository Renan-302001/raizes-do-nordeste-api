package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.MetodoPagamento;
import br.com.raizesdonordeste.api.domain.model.Pagamento;
import br.com.raizesdonordeste.api.domain.model.StatusPagamento;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PagamentoResponse {

    private final UUID idPagamento;
    private final UUID idPedido;
    private final StatusPedido statusPedido;
    private final MetodoPagamento metodoPagamento;
    private final BigDecimal valorSolicitado;
    private final StatusPagamento statusPagamento;
    private final String idTransacaoGateway;
    private final String codigoRetorno;
    private final String mensagemRetorno;
    private final Instant solicitadoEm;
    private final Instant processadoEm;

    public PagamentoResponse(Pagamento pagamento) {
        this.idPagamento = pagamento.getIdPagamento();
        this.idPedido = pagamento.getPedido().getIdPedido();
        this.statusPedido = pagamento.getPedido().getStatusPedido();
        this.metodoPagamento = pagamento.getMetodoPagamento();
        this.valorSolicitado = pagamento.getValorSolicitado();
        this.statusPagamento = pagamento.getStatusPagamento();
        this.idTransacaoGateway = pagamento.getIdTransacaoGateway();
        this.codigoRetorno = pagamento.getCodigoRetorno();
        this.mensagemRetorno = pagamento.getMensagemRetorno();
        this.solicitadoEm = pagamento.getSolicitadoEm();
        this.processadoEm = pagamento.getProcessadoEm();
    }

    public UUID getIdPagamento() { return idPagamento; }
    public UUID getIdPedido() { return idPedido; }
    public StatusPedido getStatusPedido() { return statusPedido; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public BigDecimal getValorSolicitado() { return valorSolicitado; }
    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public String getIdTransacaoGateway() { return idTransacaoGateway; }
    public String getCodigoRetorno() { return codigoRetorno; }
    public String getMensagemRetorno() { return mensagemRetorno; }
    public Instant getSolicitadoEm() { return solicitadoEm; }
    public Instant getProcessadoEm() { return processadoEm; }
}
