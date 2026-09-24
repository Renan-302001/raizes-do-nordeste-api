CREATE TABLE movimentacao_estoque (
    id_movimentacao_estoque UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_estoque_produto UUID NOT NULL,
    id_pedido UUID,
    id_usuario_responsavel UUID,
    tipo_movimentacao VARCHAR(30) NOT NULL,
    quantidade INTEGER NOT NULL,
    saldo_disponivel_anterior INTEGER NOT NULL,
    saldo_disponivel_posterior INTEGER NOT NULL,
    saldo_reservado_anterior INTEGER NOT NULL,
    saldo_reservado_posterior INTEGER NOT NULL,
    motivo VARCHAR(255),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_movimentacao_estoque_produto
        FOREIGN KEY (id_estoque_produto)
        REFERENCES estoque_produto(id_estoque_produto),

    CONSTRAINT fk_movimentacao_estoque_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido),

    CONSTRAINT fk_movimentacao_estoque_usuario
        FOREIGN KEY (id_usuario_responsavel)
        REFERENCES usuario(id_usuario),

    CONSTRAINT ck_movimentacao_estoque_tipo
        CHECK (
            tipo_movimentacao IN (
                'ENTRADA',
                'SAIDA',
                'RESERVA',
                'LIBERACAO_RESERVA',
                'BAIXA_PEDIDO'
            )
        ),

    CONSTRAINT ck_movimentacao_estoque_quantidade
        CHECK (
            quantidade > 0
            AND saldo_disponivel_anterior >= 0
            AND saldo_disponivel_posterior >= 0
            AND saldo_reservado_anterior >= 0
            AND saldo_reservado_posterior >= 0
        )
);

CREATE INDEX idx_movimentacao_estoque_produto_criado
    ON movimentacao_estoque (id_estoque_produto, criado_em DESC);
