package br.com.raizesdonordeste.api.application.gateway;

import java.math.BigDecimal;
import java.util.UUID;

public interface PagamentoGateway {

    ResultadoPagamentoGateway processar(
            UUID idPedido,
            BigDecimal valor,
            String chaveIdempotencia,
            CenarioPagamentoMock cenario
    );
}
