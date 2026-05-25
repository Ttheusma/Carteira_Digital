CREATE TABLE IF NOT EXISTS MOEDA (
    id_moeda SMALLSERIAL PRIMARY KEY,
    codigo VARCHAR(10) UNIQUE NOT NULL,
    nome VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS CARTEIRA (
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
    tipo VARCHAR(30) NOT NULL,
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


INSERT INTO MOEDA (codigo, nome, tipo) VALUES
    ('BTC',  'Bitcoin',         'CRYPTO'),
    ('ETH',  'Ethereum',        'CRYPTO'),
    ('SOL',  'Solana',          'CRYPTO'),
    ('USD',  'Dólar Americano', 'FIAT'),
    ('BRL',  'Real Brasileiro', 'FIAT')
ON CONFLICT (codigo) DO NOTHING;


CREATE OR REPLACE FUNCTION atualiza_timestamp_saldo()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_saldo_atualizacao
BEFORE UPDATE ON SALDO_CARTEIRA
FOR EACH ROW EXECUTE FUNCTION atualiza_timestamp_saldo();