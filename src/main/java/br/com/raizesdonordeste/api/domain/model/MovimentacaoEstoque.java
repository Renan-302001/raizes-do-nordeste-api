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
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_movimentacao_estoque")
    private UUID idMovimentacaoEstoque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estoque_produto", nullable = false)
    private EstoqueProduto estoqueProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsavel")
    private Usuario usuarioResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 30)
    private TipoMovimentacaoEstoque tipoMovimentacao;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "saldo_disponivel_anterior", nullable = false)
    private int saldoDisponivelAnterior;

    @Column(name = "saldo_disponivel_posterior", nullable = false)
    private int saldoDisponivelPosterior;

    @Column(name = "saldo_reservado_anterior", nullable = false)
    private int saldoReservadoAnterior;

    @Column(name = "saldo_reservado_posterior", nullable = false)
    private int saldoReservadoPosterior;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(
            EstoqueProduto estoqueProduto,
            Pedido pedido,
            Usuario usuarioResponsavel,
            TipoMovimentacaoEstoque tipoMovimentacao,
            int quantidade,
            int saldoDisponivelAnterior,
            int saldoDisponivelPosterior,
            int saldoReservadoAnterior,
            int saldoReservadoPosterior,
            String motivo
    ) {
        this.estoqueProduto = estoqueProduto;
        this.pedido = pedido;
        this.usuarioResponsavel = usuarioResponsavel;
        this.tipoMovimentacao = tipoMovimentacao;
        this.quantidade = quantidade;
        this.saldoDisponivelAnterior = saldoDisponivelAnterior;
        this.saldoDisponivelPosterior = saldoDisponivelPosterior;
        this.saldoReservadoAnterior = saldoReservadoAnterior;
        this.saldoReservadoPosterior = saldoReservadoPosterior;
        this.motivo = motivo;
    }

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = Instant.now();
    }

    public UUID getIdMovimentacaoEstoque() {
        return idMovimentacaoEstoque;
    }

    public EstoqueProduto getEstoqueProduto() {
        return estoqueProduto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Usuario getUsuarioResponsavel() {
        return usuarioResponsavel;
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

    public int getSaldoReservadoAnterior() {
        return saldoReservadoAnterior;
    }

    public int getSaldoReservadoPosterior() {
        return saldoReservadoPosterior;
    }

    public String getMotivo() {
        return motivo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
