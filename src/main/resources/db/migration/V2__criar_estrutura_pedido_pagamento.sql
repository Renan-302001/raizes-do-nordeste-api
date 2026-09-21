CREATE TABLE produto (
    id_produto UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo_publico VARCHAR(30) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    categoria VARCHAR(100) NOT NULL,
    imagem_url VARCHAR(500),
    status_produto VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encerrado_em TIMESTAMPTZ,

    CONSTRAINT ck_produto_status
        CHECK (status_produto IN ('ATIVO', 'INATIVO', 'ENCERRADO'))
);

CREATE TABLE estoque_produto (
    id_estoque_produto UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_unidade UUID NOT NULL,
    id_produto UUID NOT NULL,
    quantidade_disponivel INTEGER NOT NULL DEFAULT 0,
    quantidade_reservada INTEGER NOT NULL DEFAULT 0,
    estoque_minimo INTEGER NOT NULL DEFAULT 0,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_estoque_produto_unidade
        FOREIGN KEY (id_unidade)
        REFERENCES unidade(id_unidade),

    CONSTRAINT fk_estoque_produto_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT uq_estoque_produto_unidade_produto
        UNIQUE (id_unidade, id_produto),

    CONSTRAINT ck_estoque_produto_quantidades
        CHECK (
            quantidade_disponivel >= 0
            AND quantidade_reservada >= 0
            AND quantidade_reservada <= quantidade_disponivel
            AND estoque_minimo >= 0
        )
);

CREATE TABLE pedido (
    id_pedido UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_unidade UUID NOT NULL,
    id_cliente UUID,
    id_usuario_criador UUID,
    canal_pedido VARCHAR(20) NOT NULL,
    status_pedido VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    subtotal NUMERIC(12, 2) NOT NULL,
    valor_desconto NUMERIC(12, 2) NOT NULL DEFAULT 0,
    valor_total NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entregue_em TIMESTAMPTZ,
    cancelado_em TIMESTAMPTZ,

    CONSTRAINT fk_pedido_unidade
        FOREIGN KEY (id_unidade)
        REFERENCES unidade(id_unidade),

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente),

    CONSTRAINT fk_pedido_usuario_criador
        FOREIGN KEY (id_usuario_criador)
        REFERENCES usuario(id_usuario),

    CONSTRAINT ck_pedido_canal
        CHECK (canal_pedido IN ('APP', 'TOTEM', 'BALCAO', 'PICKUP', 'WEB')),

    CONSTRAINT ck_pedido_status
        CHECK (
            status_pedido IN (
                'AGUARDANDO_PAGAMENTO',
                'PAGAMENTO_RECUSADO',
                'CONFIRMADO',
                'EM_PREPARO',
                'PRONTO',
                'ENTREGUE',
                'CANCELADO'
            )
        ),

    CONSTRAINT ck_pedido_valores
        CHECK (
            subtotal >= 0
            AND valor_desconto >= 0
            AND valor_desconto <= subtotal
            AND valor_total = subtotal - valor_desconto
        )
);

CREATE TABLE item_pedido (
    id_item_pedido UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_pedido UUID NOT NULL,
    id_produto UUID NOT NULL,
    nome_produto VARCHAR(150) NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(12, 2) NOT NULL,
    valor_desconto_item NUMERIC(12, 2) NOT NULL DEFAULT 0,
    valor_total_item NUMERIC(12, 2) NOT NULL,

    CONSTRAINT fk_item_pedido_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_pedido_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT uq_item_pedido_pedido_produto
        UNIQUE (id_pedido, id_produto),

    CONSTRAINT ck_item_pedido_valores
        CHECK (
            quantidade > 0
            AND preco_unitario >= 0
            AND valor_desconto_item >= 0
            AND valor_total_item = (preco_unitario * quantidade) - valor_desconto_item
        )
);

CREATE TABLE pagamento (
    id_pagamento UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_pedido UUID NOT NULL,
    chave_idempotencia VARCHAR(100) NOT NULL UNIQUE,
    id_transacao_gateway VARCHAR(100) UNIQUE,
    metodo_pagamento VARCHAR(30) NOT NULL DEFAULT 'MOCK',
    valor_solicitado NUMERIC(12, 2) NOT NULL,
    status_pagamento VARCHAR(20) NOT NULL DEFAULT 'SOLICITADO',
    codigo_retorno VARCHAR(50),
    mensagem_retorno VARCHAR(255),
    solicitado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processado_em TIMESTAMPTZ,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pagamento_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido),

    CONSTRAINT ck_pagamento_metodo
        CHECK (metodo_pagamento IN ('MOCK')),

    CONSTRAINT ck_pagamento_status
        CHECK (status_pagamento IN ('SOLICITADO', 'APROVADO', 'RECUSADO', 'ERRO')),

    CONSTRAINT ck_pagamento_valor
        CHECK (valor_solicitado > 0)
);

CREATE INDEX idx_pedido_unidade_status
    ON pedido (id_unidade, status_pedido);

CREATE INDEX idx_pedido_canal
    ON pedido (canal_pedido);

CREATE INDEX idx_pagamento_pedido
    ON pagamento (id_pedido);
