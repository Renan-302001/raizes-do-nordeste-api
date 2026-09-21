package br.com.raizesdonordeste.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstoqueProdutoTest {

    @Test
    void deveReservarEConfirmarSaidaDoEstoque() {
        EstoqueProduto estoque = novoEstoque(10);

        estoque.reservar(4);

        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(4, estoque.getQuantidadeReservada());
        assertEquals(6, estoque.getQuantidadeParaVenda());

        estoque.confirmarSaida(4);

        assertEquals(6, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
        assertEquals(6, estoque.getQuantidadeParaVenda());
    }

    @Test
    void deveLiberarReservaSemReduzirQuantidadeFisica() {
        EstoqueProduto estoque = novoEstoque(10);
        estoque.reservar(3);

        estoque.liberarReserva(3);

        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
    }

    @Test
    void deveRejeitarReservaAcimaDoSaldoParaVenda() {
        EstoqueProduto estoque = novoEstoque(5);
        estoque.reservar(4);

        assertThrows(
                IllegalStateException.class,
                () -> estoque.reservar(2)
        );
    }

    private EstoqueProduto novoEstoque(int quantidade) {
        return new EstoqueProduto(
                novaUnidade(),
                novoProduto(),
                quantidade,
                2
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
                "Cuscuz de teste",
                "CAFÉ DA MANHÃ",
                null
        );
    }
}
