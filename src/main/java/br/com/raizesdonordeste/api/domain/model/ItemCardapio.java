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
@Table(name = "item_cardapio")
public class ItemCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_item_cardapio")
    private UUID idItemCardapio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cardapio", nullable = false)
    private Cardapio cardapio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_item", nullable = false, length = 20)
    private StatusItemCardapio statusItem = StatusItemCardapio.DISPONIVEL;

    @Column(name = "inicio_vigencia")
    private Instant inicioVigencia;

    @Column(name = "fim_vigencia")
    private Instant fimVigencia;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "retirado_em")
    private Instant retiradoEm;

    protected ItemCardapio() {
    }

    public ItemCardapio(
            Cardapio cardapio,
            Produto produto,
            BigDecimal preco,
            Instant inicioVigencia,
            Instant fimVigencia
    ) {
        this.cardapio = cardapio;
        this.produto = produto;
        this.preco = preco;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
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

    public boolean estaDisponivelEm(Instant momento) {
        boolean vigenciaIniciada = inicioVigencia == null || !momento.isBefore(inicioVigencia);
        boolean vigenciaNaoEncerrada = fimVigencia == null || momento.isBefore(fimVigencia);

        return statusItem == StatusItemCardapio.DISPONIVEL
                && retiradoEm == null
                && vigenciaIniciada
                && vigenciaNaoEncerrada;
    }

    public void disponibilizar() {
        this.statusItem = StatusItemCardapio.DISPONIVEL;
        this.retiradoEm = null;
    }

    public void indisponibilizar() {
        this.statusItem = StatusItemCardapio.INDISPONIVEL;
    }

    public void retirar() {
        this.statusItem = StatusItemCardapio.RETIRADO;
        this.retiradoEm = Instant.now();
    }

    public UUID getIdItemCardapio() {
        return idItemCardapio;
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    public Produto getProduto() {
        return produto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public StatusItemCardapio getStatusItem() {
        return statusItem;
    }

    public Instant getInicioVigencia() {
        return inicioVigencia;
    }

    public Instant getFimVigencia() {
        return fimVigencia;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public Instant getRetiradoEm() {
        return retiradoEm;
    }
}
