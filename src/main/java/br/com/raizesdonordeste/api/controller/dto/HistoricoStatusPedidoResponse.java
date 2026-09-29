package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.HistoricoStatusPedido;
import br.com.raizesdonordeste.api.domain.model.OrigemAlteracaoPedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;

import java.time.Instant;
import java.util.UUID;

public class HistoricoStatusPedidoResponse {

    private final UUID idHistoricoStatus;
    private final UUID idUsuarioResponsavel;
    private final StatusPedido statusAnterior;
    private final StatusPedido statusNovo;
    private final OrigemAlteracaoPedido origemAlteracao;
    private final String observacao;
    private final Instant alteradoEm;

    public HistoricoStatusPedidoResponse(HistoricoStatusPedido historico) {
        this.idHistoricoStatus = historico.getIdHistoricoStatus();
        this.idUsuarioResponsavel = historico.getUsuarioResponsavel() == null
                ? null : historico.getUsuarioResponsavel().getIdUsuario();
        this.statusAnterior = historico.getStatusAnterior();
        this.statusNovo = historico.getStatusNovo();
        this.origemAlteracao = historico.getOrigemAlteracao();
        this.observacao = historico.getObservacao();
        this.alteradoEm = historico.getAlteradoEm();
    }

    public UUID getIdHistoricoStatus() { return idHistoricoStatus; }
    public UUID getIdUsuarioResponsavel() { return idUsuarioResponsavel; }
    public StatusPedido getStatusAnterior() { return statusAnterior; }
    public StatusPedido getStatusNovo() { return statusNovo; }
    public OrigemAlteracaoPedido getOrigemAlteracao() { return origemAlteracao; }
    public String getObservacao() { return observacao; }
    public Instant getAlteradoEm() { return alteradoEm; }
}
