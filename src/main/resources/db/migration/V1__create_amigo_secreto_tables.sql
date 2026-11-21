-- Um grupo por instalação: a configuração é uma linha só (id = 1).
CREATE TABLE IF NOT EXISTS tb_configuracao (
    id            SMALLINT PRIMARY KEY CHECK (id = 1),
    titulo        VARCHAR(50)  NOT NULL,
    data_sorteio  DATE,
    data_troca    DATE,
    valor         INTEGER CHECK (valor IS NULL OR valor >= 0),
    status        VARCHAR(10)  NOT NULL DEFAULT 'OPEN',
    sorteado_em   TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

INSERT INTO tb_configuracao (id, titulo, data_troca, status)
VALUES (1, 'Natal 2026', DATE '2026-12-24', 'OPEN')
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS tb_participante (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(40)  NOT NULL,
    foto          TEXT,
    calcado       VARCHAR(16)  NOT NULL DEFAULT '',
    calca         VARCHAR(16)  NOT NULL DEFAULT '',
    camisa        VARCHAR(16)  NOT NULL DEFAULT '',
    gostos        VARCHAR(300) NOT NULL DEFAULT '',
    lista_desejos VARCHAR(500) NOT NULL DEFAULT '',
    evitar        VARCHAR(160) NOT NULL DEFAULT '',
    manual        BOOLEAN      NOT NULL DEFAULT FALSE,
    -- Só o hash SHA-256 do token pessoal fica no banco.
    token_hash    CHAR(64)     NOT NULL UNIQUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Quem tirou quem. Apagada e regravada a cada sorteio.
-- Se quem foi tirado sair, o par fica com receiver nulo (motivo "receiver_left").
CREATE TABLE IF NOT EXISTS tb_atribuicao (
    giver_id    BIGINT PRIMARY KEY REFERENCES tb_participante (id) ON DELETE CASCADE,
    receiver_id BIGINT REFERENCES tb_participante (id) ON DELETE SET NULL
);

-- Quem entrou no último sorteio.
CREATE TABLE IF NOT EXISTS tb_sorteio_membro (
    participante_id BIGINT PRIMARY KEY REFERENCES tb_participante (id) ON DELETE CASCADE
);
