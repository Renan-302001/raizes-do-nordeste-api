CREATE TABLE historico_status_pedido (
    id_historico_status UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_pedido UUID NOT NULL,
    id_usuario_responsavel UUID,
    status_anterior VARCHAR(30),
    status_novo VARCHAR(30) NOT NULL,
    origem_alteracao VARCHAR(30) NOT NULL,
    observacao VARCHAR(255),
    alterado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historico_status_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),

    CONSTRAINT fk_historico_status_usuario
        FOREIGN KEY (id_usuario_responsavel) REFERENCES usuario(id_usuario),

    CONSTRAINT ck_historico_status_anterior
        CHECK (
            status_anterior IS NULL OR status_anterior IN (
                'AGUARDANDO_PAGAMENTO', 'PAGAMENTO_RECUSADO', 'CONFIRMADO',
                'EM_PREPARO', 'PRONTO', 'ENTREGUE', 'CANCELADO'
            )
        ),

    CONSTRAINT ck_historico_status_novo
        CHECK (
            status_novo IN (
                'AGUARDANDO_PAGAMENTO', 'PAGAMENTO_RECUSADO', 'CONFIRMADO',
                'EM_PREPARO', 'PRONTO', 'ENTREGUE', 'CANCELADO'
            )
        ),

    CONSTRAINT ck_historico_status_origem
        CHECK (origem_alteracao IN ('CRIACAO', 'PAGAMENTO', 'OPERACAO', 'CANCELAMENTO'))
);

CREATE INDEX idx_historico_status_pedido_data
    ON historico_status_pedido (id_pedido, alterado_em);
