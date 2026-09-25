package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PedidoResponse {

    private final UUID idPedido;
    private final UUID idUnidade;
    private final UUID idCliente;
    private final UUID idUsuarioCriador;
    private final CanalPedido canalPedido;
    private final StatusPedido statusPedido;
    private final BigDecimal subtotal;
    private final BigDecimal valorDesconto;
    private final BigDecimal valorTotal;
    private final Instant criadoEm;
    private final List<ItemPedidoResponse> itens;

    public PedidoResponse(Pedido pedido) {
        this.idPedido = pedido.getIdPedido();
        this.idUnidade = pedido.getUnidade().getIdUnidade();
        this.idCliente = pedido.getCliente() == null
                ? null : pedido.getCliente().getIdUsuario();
        this.idUsuarioCriador = pedido.getUsuarioCriador() == null
                ? null : pedido.getUsuarioCriador().getIdUsuario();
        this.canalPedido = pedido.getCanalPedido();
        this.statusPedido = pedido.getStatusPedido();
        this.subtotal = pedido.getSubtotal();
        this.valorDesconto = pedido.getValorDesconto();
        this.valorTotal = pedido.getValorTotal();
        this.criadoEm = pedido.getCriadoEm();
        this.itens = pedido.getItens().stream()
                .map(ItemPedidoResponse::new)
                .toList();
    }

    public UUID getIdPedido() { return idPedido; }
    public UUID getIdUnidade() { return idUnidade; }
    public UUID getIdCliente() { return idCliente; }
    public UUID getIdUsuarioCriador() { return idUsuarioCriador; }
    public CanalPedido getCanalPedido() { return canalPedido; }
    public StatusPedido getStatusPedido() { return statusPedido; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public Instant getCriadoEm() { return criadoEm; }
    public List<ItemPedidoResponse> getItens() { return itens; }
}
