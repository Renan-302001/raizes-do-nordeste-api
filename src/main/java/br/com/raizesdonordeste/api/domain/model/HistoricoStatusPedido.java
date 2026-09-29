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
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historico_status_pedido")
public class HistoricoStatusPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historico_status")
    private UUID idHistoricoStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsavel")
    private Usuario usuarioResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusPedido statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusPedido statusNovo;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_alteracao", nullable = false, length = 30)
    private OrigemAlteracaoPedido origemAlteracao;

    @Column(name = "observacao", length = 255)
    private String observacao;

    @Column(name = "alterado_em", nullable = false)
    private Instant alteradoEm;

    protected HistoricoStatusPedido() {
    }

    public HistoricoStatusPedido(
            Pedido pedido,
            Usuario usuarioResponsavel,
            StatusPedido statusAnterior,
            StatusPedido statusNovo,
            OrigemAlteracaoPedido origemAlteracao,
            String observacao
    ) {
        this.pedido = pedido;
        this.usuarioResponsavel = usuarioResponsavel;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.origemAlteracao = origemAlteracao;
        this.observacao = observacao;
    }

    @PrePersist
    protected void aoCriar() {
        this.alteradoEm = Instant.now();
    }

    public UUID getIdHistoricoStatus() { return idHistoricoStatus; }
    public Pedido getPedido() { return pedido; }
    public Usuario getUsuarioResponsavel() { return usuarioResponsavel; }
    public StatusPedido getStatusAnterior() { return statusAnterior; }
    public StatusPedido getStatusNovo() { return statusNovo; }
    public OrigemAlteracaoPedido getOrigemAlteracao() { return origemAlteracao; }
    public String getObservacao() { return observacao; }
    public Instant getAlteradoEm() { return alteradoEm; }
}
