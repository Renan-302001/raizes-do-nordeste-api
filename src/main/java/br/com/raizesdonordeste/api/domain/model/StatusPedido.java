package br.com.raizesdonordeste.api.domain.model;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO,
    PAGAMENTO_RECUSADO,
    CONFIRMADO,
    EM_PREPARO,
    PRONTO,
    ENTREGUE,
    CANCELADO
}
