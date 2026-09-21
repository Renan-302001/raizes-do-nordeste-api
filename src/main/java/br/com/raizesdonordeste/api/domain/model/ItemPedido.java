package br.com.raizesdonordeste.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_item_pedido")
    private UUID idItemPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "nome_produto", nullable = false, length = 150)
    private String nomeProduto;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "valor_desconto_item", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorDescontoItem = BigDecimal.ZERO;

    @Column(name = "valor_total_item", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotalItem;

    protected ItemPedido() {
    }

    ItemPedido(
            Pedido pedido,
            Produto produto,
            int quantidade,
            BigDecimal precoUnitario
    ) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
        }

        if (precoUnitario == null || precoUnitario.signum() <= 0) {
            throw new IllegalArgumentException("O preço do item deve ser maior que zero.");
        }

        this.pedido = pedido;
        this.produto = produto;
        this.nomeProduto = produto.getNome();
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotalItem = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public UUID getIdItemPedido() {
        return idItemPedido;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getValorDescontoItem() {
        return valorDescontoItem;
    }

    public BigDecimal getValorTotalItem() {
        return valorTotalItem;
    }
}
