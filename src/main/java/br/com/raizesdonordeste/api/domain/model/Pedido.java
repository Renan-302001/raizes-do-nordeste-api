package br.com.raizesdonordeste.api.domain.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pedido")
    private UUID idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_criador")
    private Usuario usuarioCriador;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido", nullable = false, length = 20)
    private CanalPedido canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false, length = 30)
    private StatusPedido statusPedido = StatusPedido.AGUARDANDO_PAGAMENTO;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "valor_desconto", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "entregue_em")
    private Instant entregueEm;

    @Column(name = "cancelado_em")
    private Instant canceladoEm;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemPedido> itens = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(
            Unidade unidade,
            Cliente cliente,
            Usuario usuarioCriador,
            CanalPedido canalPedido
    ) {
        if (unidade == null || canalPedido == null) {
            throw new IllegalArgumentException("Unidade e canal do pedido são obrigatórios.");
        }

        this.unidade = unidade;
        this.cliente = cliente;
        this.usuarioCriador = usuarioCriador;
        this.canalPedido = canalPedido;
    }

    @PrePersist
    protected void aoCriar() {
        Instant agora = Instant.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    public void adicionarItem(
            Produto produto,
            int quantidade,
            BigDecimal precoUnitario
    ) {
        if (statusPedido != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Não é possível alterar os itens deste pedido.");
        }

        boolean produtoJaAdicionado = itens.stream()
                .map(ItemPedido::getProduto)
                .anyMatch(produtoExistente ->
                        produtoExistente == produto
                                || produto.getIdProduto() != null
                                && produto.getIdProduto().equals(produtoExistente.getIdProduto())
                );

        if (produtoJaAdicionado) {
            throw new IllegalArgumentException("O produto está duplicado no pedido.");
        }

        itens.add(new ItemPedido(this, produto, quantidade, precoUnitario));
        recalcularTotais();
    }

    private void recalcularTotais() {
        this.subtotal = itens.stream()
                .map(ItemPedido::getValorTotalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.valorTotal = subtotal.subtract(valorDesconto);
    }

    public void registrarPagamentoAprovado() {
        exigirStatusParaPagamento();
        this.statusPedido = StatusPedido.CONFIRMADO;
    }

    public void registrarPagamentoRecusado() {
        exigirStatusParaPagamento();
        this.statusPedido = StatusPedido.PAGAMENTO_RECUSADO;
    }

    private void exigirStatusParaPagamento() {
        if (statusPedido != StatusPedido.AGUARDANDO_PAGAMENTO
                && statusPedido != StatusPedido.PAGAMENTO_RECUSADO) {
            throw new IllegalStateException("O pedido não aceita processamento de pagamento.");
        }
    }

    public void iniciarPreparo() {
        exigirStatus(StatusPedido.CONFIRMADO);
        this.statusPedido = StatusPedido.EM_PREPARO;
    }

    public void marcarComoPronto() {
        exigirStatus(StatusPedido.EM_PREPARO);
        this.statusPedido = StatusPedido.PRONTO;
    }

    public void registrarEntrega() {
        exigirStatus(StatusPedido.PRONTO);
        this.statusPedido = StatusPedido.ENTREGUE;
        this.entregueEm = Instant.now();
    }

    public void cancelar() {
        if (statusPedido == StatusPedido.ENTREGUE
                || statusPedido == StatusPedido.CANCELADO) {
            throw new IllegalStateException("O pedido não pode ser cancelado neste estado.");
        }

        this.statusPedido = StatusPedido.CANCELADO;
        this.canceladoEm = Instant.now();
    }

    private void exigirStatus(StatusPedido statusEsperado) {
        if (statusPedido != statusEsperado) {
            throw new IllegalStateException(
                    "Transição inválida para o estado atual do pedido."
            );
        }
    }

    public UUID getIdPedido() {
        return idPedido;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Usuario getUsuarioCriador() {
        return usuarioCriador;
    }

    public CanalPedido getCanalPedido() {
        return canalPedido;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public Instant getEntregueEm() {
        return entregueEm;
    }

    public Instant getCanceladoEm() {
        return canceladoEm;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }
}
