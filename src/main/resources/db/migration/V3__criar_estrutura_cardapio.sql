CREATE TABLE cardapio (
    id_cardapio UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_unidade UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    status_cardapio VARCHAR(20) NOT NULL DEFAULT 'INATIVO',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cardapio_unidade
        FOREIGN KEY (id_unidade)
        REFERENCES unidade(id_unidade),

    CONSTRAINT uq_cardapio_unidade_nome
        UNIQUE (id_unidade, nome),

    CONSTRAINT ck_cardapio_status
        CHECK (status_cardapio IN ('ATIVO', 'INATIVO'))
);

CREATE UNIQUE INDEX uq_cardapio_ativo_por_unidade
    ON cardapio (id_unidade)
    WHERE status_cardapio = 'ATIVO';

CREATE TABLE item_cardapio (
    id_item_cardapio UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_cardapio UUID NOT NULL,
    id_produto UUID NOT NULL,
    preco NUMERIC(12, 2) NOT NULL,
    status_item VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
    inicio_vigencia TIMESTAMPTZ,
    fim_vigencia TIMESTAMPTZ,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retirado_em TIMESTAMPTZ,

    CONSTRAINT fk_item_cardapio_cardapio
        FOREIGN KEY (id_cardapio)
        REFERENCES cardapio(id_cardapio)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_cardapio_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT uq_item_cardapio_cardapio_produto
        UNIQUE (id_cardapio, id_produto),

    CONSTRAINT ck_item_cardapio_preco
        CHECK (preco > 0),

    CONSTRAINT ck_item_cardapio_status
        CHECK (status_item IN ('DISPONIVEL', 'INDISPONIVEL', 'RETIRADO')),

    CONSTRAINT ck_item_cardapio_vigencia
        CHECK (
            inicio_vigencia IS NULL
            OR fim_vigencia IS NULL
            OR fim_vigencia > inicio_vigencia
        )
);

CREATE INDEX idx_item_cardapio_produto
    ON item_cardapio (id_produto);
