package br.com.raizesdonordeste.api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemCardapioTest {

    @Test
    void deveEstarDisponivelDuranteAVigencia() {
        Instant agora = Instant.now();
        ItemCardapio item = novoItem(
                agora.minusSeconds(60),
                agora.plusSeconds(60)
        );

        assertTrue(item.estaDisponivelEm(agora));
    }

    @Test
    void naoDeveEstarDisponivelAntesDaVigencia() {
        Instant agora = Instant.now();
        ItemCardapio item = novoItem(
                agora.plusSeconds(60),
                agora.plusSeconds(120)
        );

        assertFalse(item.estaDisponivelEm(agora));
    }

    @Test
    void naoDeveEstarDisponivelQuandoFoiIndisponibilizado() {
        Instant agora = Instant.now();
        ItemCardapio item = novoItem(null, null);
        item.indisponibilizar();

        assertFalse(item.estaDisponivelEm(agora));
    }

    private ItemCardapio novoItem(Instant inicio, Instant fim) {
        Unidade unidade = new Unidade(
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

        Cardapio cardapio = new Cardapio(unidade, "Cardápio Teste");
        Produto produto = new Produto(
                "PROD-TESTE",
                "Cuscuz",
                null,
                "CAFÉ DA MANHÃ",
                null
        );

        return new ItemCardapio(
                cardapio,
                produto,
                new BigDecimal("12.50"),
                inicio,
                fim
        );
    }
}
