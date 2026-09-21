package br.com.raizesdonordeste.api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagamentoTest {

    @Test
    void deveRegistrarAprovacaoDoPagamento() {
        Pagamento pagamento = novoPagamento();

        pagamento.aprovar("gateway-123", "00", "Pagamento aprovado.");

        assertEquals(StatusPagamento.APROVADO, pagamento.getStatusPagamento());
        assertEquals("gateway-123", pagamento.getIdTransacaoGateway());
        assertEquals("00", pagamento.getCodigoRetorno());
        assertNotNull(pagamento.getProcessadoEm());
    }

    @Test
    void deveImpedirSegundoProcessamentoDoMesmoPagamento() {
        Pagamento pagamento = novoPagamento();
        pagamento.recusar("gateway-123", "51", "Pagamento recusado.");

        assertThrows(
                IllegalStateException.class,
                () -> pagamento.aprovar("gateway-456", "00", "Pagamento aprovado.")
        );
    }

    private Pagamento novoPagamento() {
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

        Pedido pedido = new Pedido(unidade, null, null, CanalPedido.APP);

        return new Pagamento(
                pedido,
                "chave-idempotente",
                new BigDecimal("25.00")
        );
    }
}
