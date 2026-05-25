CREATE TABLE IF NOT EXISTS MOEDA (
    id_moeda SMALLSERIAL PRIMARY KEY,
    codigo VARCHAR(10) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS CARTEIRA(
    endereco_carteira VARCHAR(255) PRIMARY KEY,
    hash_chave_privada VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ATIVA'
);

CREATE TABLE IF NOT EXISTS SALDO_CARTEIRA (
    endereco_carteira VARCHAR(255) NOT NULL,
    id_moeda SMALLINT NOT NULL,
    saldo NUMERIC(20, 8) DEFAULT 0.00000000 CHECK (saldo >= 0),
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (endereco_carteira, id_moeda),
    CONSTRAINT fk_saldo_carteira FOREIGN KEY (endereco_carteira) REFERENCES CARTEIRA(endereco_carteira) ON DELETE CASCADE,
    CONSTRAINT fk_saldo_moeda FOREIGN KEY (id_moeda) REFERENCES MOEDA(id_moeda) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS DEPOSITO_SAQUE (
    id_movimento BIGSERIAL PRIMARY KEY,
    endereco_carteira VARCHAR(255) NOT NULL,
    id_moeda SMALLINT NOT NULL,
    tipo VARCHAR(30) NOT NULL, -- Ex: DEPOSITO, SAQUE
    valor NUMERIC(20, 8) NOT NULL CHECK (valor > 0),
    taxa_valor NUMERIC(20, 8) DEFAULT 0.00000000,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimento_carteira FOREIGN KEY (endereco_carteira) REFERENCES CARTEIRA(endereco_carteira),
    CONSTRAINT fk_movimento_moeda FOREIGN KEY (id_moeda) REFERENCES MOEDA(id_moeda)
);

CREATE TABLE IF NOT EXISTS CONVERSAO (
    id_conversao BIGSERIAL PRIMARY KEY,
    endereco_carteira VARCHAR(255) NOT NULL,
    id_moeda_origem SMALLINT NOT NULL,
    id_moeda_destino SMALLINT NOT NULL,
    valor_origem NUMERIC(20, 8) NOT NULL CHECK (valor_origem > 0),
    valor_destino NUMERIC(20, 8) NOT NULL CHECK (valor_destino > 0),
    taxa_percentual NUMERIC(5, 4) NOT NULL,
    taxa_valor NUMERIC(20, 8) NOT NULL,
    cotacao_utilizada NUMERIC(20, 8) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conversao_carteira FOREIGN KEY (endereco_carteira) REFERENCES CARTEIRA(endereco_carteira),
    CONSTRAINT fk_conversao_moeda_origem FOREIGN KEY (id_moeda_origem) REFERENCES MOEDA(id_moeda),
    CONSTRAINT fk_conversao_moeda_destino FOREIGN KEY (id_moeda_destino) REFERENCES MOEDA(id_moeda)
);

CREATE TABLE IF NOT EXISTS TRANSFERENCIA (
    id_transferencia BIGSERIAL PRIMARY KEY,
    endereco_origem VARCHAR(255) NOT NULL,
    endereco_destino VARCHAR(255) NOT NULL,
    id_moeda SMALLINT NOT NULL,
    valor NUMERIC(20, 8) NOT NULL CHECK (valor > 0),
    taxa_valor NUMERIC(20, 8) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transf_origem FOREIGN KEY (endereco_origem) REFERENCES CARTEIRA(endereco_carteira),
    CONSTRAINT fk_transf_destino FOREIGN KEY (endereco_destino) REFERENCES CARTEIRA(endereco_carteira),
    CONSTRAINT fk_transf_moeda FOREIGN KEY (id_moeda) REFERENCES MOEDA(id_moeda)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'wallet_api_homolog') THEN
        CREATE ROLE wallet_api_homolog WITH LOGIN PASSWORD 'api123';
    END IF;
END
$$;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;

GRANT USAGE ON SCHEMA public TO wallet_api_homolog;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO wallet_api_homolog;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO wallet_api_homolog;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO wallet_api_homolog;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO wallet_api_homolog;