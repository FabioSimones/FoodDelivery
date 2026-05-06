CREATE TABLE restaurantes (
                              id UUID PRIMARY KEY,
                              nome VARCHAR(150) NOT NULL,
                              cnpj VARCHAR(20),
                              endereco VARCHAR(255),
                              ativo BOOLEAN NOT NULL DEFAULT TRUE,
                              criado_em TIMESTAMP NOT NULL,
                              atualizado_em TIMESTAMP
);

CREATE UNIQUE INDEX uk_restaurantes_cnpj
    ON restaurantes (cnpj)
    WHERE cnpj IS NOT NULL;