CREATE TABLE perfil (
    id_perfil UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo_perfil VARCHAR(50) NOT NULL UNIQUE,
    nome_perfil VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    tipo_escopo VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT ck_perfil_tipo_escopo
                    CHECK ( tipo_escopo IN ('REDE', 'UNIDADE'))
);

CREATE TABLE usuario (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    data_nascimento DATE NOT NULL,
    status_conta VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encerrado_em TIMESTAMPTZ,

    CONSTRAINT ck_usuario_status_conta
                     CHECK ( status_conta IN ('ATIVA', 'INATIVA', 'BLOQUEADA', 'ENCERRADA') )
);

CREATE TABLE cliente (
    id_cliente UUID PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,

    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (id_cliente)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE unidade (
    id_unidade UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    codigo_publico VARCHAR(30) NOT NULL UNIQUE,
    logradouro VARCHAR(150) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    status_unidade VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encerrado_em TIMESTAMPTZ,

    CONSTRAINT ck_unidade_status
                     CHECK ( status_unidade IN ('ATIVA', 'INATIVA', 'ENCERRADA'))
);

CREATE TABLE funcionario (
    id_funcionario UUID PRIMARY KEY,
    id_unidade UUID NOT NULL,
    id_perfil UUID NOT NULL,
    matricula VARCHAR(30) NOT NULL UNIQUE,
    data_admissao DATE NOT NULL,
    data_desligamento DATE,
    status_vinculo VARCHAR(20) NOT NULL DEFAULT 'ATIVO',

    CONSTRAINT fk_funcionario_usuario
        FOREIGN KEY (id_funcionario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_funcionario_unidade
        FOREIGN KEY (id_unidade)
        REFERENCES unidade(id_unidade),

    CONSTRAINT fk_funcionario_perfil
        FOREIGN KEY (id_perfil)
        REFERENCES perfil(id_perfil),

    CONSTRAINT ck_funcionario_status_vinculo
        CHECK (status_vinculo IN ('ATIVO', 'AFASTADO', 'DESLIGADO'))
);

CREATE TABLE horario_funcionamento (
    id_horario_funcionamento UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_unidade UUID NOT NULL,
    dia_semana VARCHAR(15) NOT NULL,
    hora_abertura TIME,
    hora_fechamento TIME,
    fechado BOOLEAN NOT NULL DEFAULT FALSE,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_horario_unidade
        FOREIGN KEY (id_unidade)
        REFERENCES unidade(id_unidade),

    CONSTRAINT ck_horario_dia_semana
        CHECK ( dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO')),

    CONSTRAINT ck_horario_preenchimento
        CHECK (
            (
                fechado = TRUE
                    AND hora_abertura IS NULL
                    AND hora_fechamento IS NULL
                )
                OR
            (
                fechado = FALSE
                    AND hora_abertura IS NOT NULL
                    AND hora_fechamento IS NOT NULL
                )
            ),

    CONSTRAINT uq_horario_unidade_dia
        UNIQUE (id_unidade, dia_semana)

);