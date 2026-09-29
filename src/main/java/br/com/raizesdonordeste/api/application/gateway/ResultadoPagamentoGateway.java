package br.com.raizesdonordeste.api.application.gateway;

import br.com.raizesdonordeste.api.domain.model.StatusPagamento;

public record ResultadoPagamentoGateway(
        StatusPagamento status,
        String idTransacao,
        String codigo,
        String mensagem
) {
}
