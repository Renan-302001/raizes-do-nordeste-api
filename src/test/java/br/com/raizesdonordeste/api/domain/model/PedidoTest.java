package br.com.raizesdonordeste.api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    @Test
    void deveAdicionarItemECalcularTotais() {
        Pedido pedido = novoPedido();
        Produto produto = novoProduto();

        pedido.adicionarItem(produto, 2, new BigDecimal("12.50"));

        assertEquals(new BigDecimal("25.00"), pedido.getSubtotal());
        assertEquals(new BigDecimal("25.00"), pedido.getValorTotal());
        assertEquals(1, pedido.getItens().size());
    }

    @Test
    void deveExecutarFluxoDeStatusAteAEntrega() {
        Pedido pedido = novoPedido();

        pedido.registrarPagamentoAprovado();
        assertEquals(StatusPedido.CONFIRMADO, pedido.getStatusPedido());

        pedido.iniciarPreparo();
        assertEquals(StatusPedido.EM_PREPARO, pedido.getStatusPedido());

        pedido.marcarComoPronto();
        assertEquals(StatusPedido.PRONTO, pedido.getStatusPedido());

        pedido.registrarEntrega();
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatusPedido());
    }

    @Test
    void deveRejeitarTransicaoDeStatusForaDaOrdem() {
        Pedido pedido = novoPedido();

        assertThrows(
                IllegalStateException.class,
                pedido::marcarComoPronto
        );
    }

    @Test
    void deveRejeitarProdutoDuplicado() {
        Pedido pedido = novoPedido();
        Produto produto = novoProduto();
        pedido.adicionarItem(produto, 1, new BigDecimal("12.50"));

        assertThrows(
                IllegalArgumentException.class,
                () -> pedido.adicionarItem(produto, 1, new BigDecimal("12.50"))
        );
    }

    private Pedido novoPedido() {
        return new Pedido(
                novaUnidade(),
                null,
                null,
                CanalPedido.APP
        );
    }

    private Unidade novaUnidade() {
        return new Unidade(
                "Unidade Teste",
                "12345678000199",
                "UNI-TESTE",
                "Rua Teste",
                "10",
                null,
                "Centro",
                "Recife",
                "PE",
                "50000000"
        );
    }

    private Produto novoProduto() {
        return new Produto(
                "PROD-TESTE",
                "Cuscuz",
                null,
                "CAFÉ DA MANHÃ",
                null
        );
    }
}
