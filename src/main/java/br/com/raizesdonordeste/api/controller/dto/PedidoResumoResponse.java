package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PedidoResumoResponse {

    private final UUID idPedido;
    private final UUID idUnidade;
    private final CanalPedido canalPedido;
    private final StatusPedido statusPedido;
    private final BigDecimal valorTotal;
    private final Instant criadoEm;

    public PedidoResumoResponse(Pedido pedido) {
        this.idPedido = pedido.getIdPedido();
        this.idUnidade = pedido.getUnidade().getIdUnidade();
        this.canalPedido = pedido.getCanalPedido();
        this.statusPedido = pedido.getStatusPedido();
        this.valorTotal = pedido.getValorTotal();
        this.criadoEm = pedido.getCriadoEm();
    }

    public UUID getIdPedido() { return idPedido; }
    public UUID getIdUnidade() { return idUnidade; }
    public CanalPedido getCanalPedido() { return canalPedido; }
    public StatusPedido getStatusPedido() { return statusPedido; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public Instant getCriadoEm() { return criadoEm; }
}
