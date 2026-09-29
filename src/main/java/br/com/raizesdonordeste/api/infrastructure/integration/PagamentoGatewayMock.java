package br.com.raizesdonordeste.api.infrastructure.integration;

import br.com.raizesdonordeste.api.application.gateway.CenarioPagamentoMock;
import br.com.raizesdonordeste.api.application.gateway.PagamentoGateway;
import br.com.raizesdonordeste.api.application.gateway.ResultadoPagamentoGateway;
import br.com.raizesdonordeste.api.domain.model.StatusPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PagamentoGatewayMock implements PagamentoGateway {

    @Override
    public ResultadoPagamentoGateway processar(
            UUID idPedido,
            BigDecimal valor,
            String chaveIdempotencia,
            CenarioPagamentoMock cenario
    ) {
        return switch (cenario) {
            case APROVADO -> new ResultadoPagamentoGateway(
                    StatusPagamento.APROVADO,
                    "mock-" + UUID.randomUUID(),
                    "00",
                    "Pagamento aprovado pelo gateway mock."
            );
            case RECUSADO -> new ResultadoPagamentoGateway(
                    StatusPagamento.RECUSADO,
                    "mock-" + UUID.randomUUID(),
                    "51",
                    "Pagamento recusado pelo gateway mock."
            );
            case ERRO -> new ResultadoPagamentoGateway(
                    StatusPagamento.ERRO,
                    null,
                    "GATEWAY_UNAVAILABLE",
                    "O gateway mock está temporariamente indisponível."
            );
        };
    }
}
