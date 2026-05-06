CREATE TABLE categorias (
                            id UUID PRIMARY KEY,
                            restaurante_id UUID NOT NULL,
                            nome VARCHAR(120) NOT NULL,
                            descricao VARCHAR(255),
                            ordem_exibicao INTEGER NOT NULL DEFAULT 0,
                            ativa BOOLEAN NOT NULL DEFAULT TRUE,
                            criado_em TIMESTAMP NOT NULL,
                            atualizado_em TIMESTAMP,

                            CONSTRAINT fk_categorias_restaurantes
                                FOREIGN KEY (restaurante_id)
                                    REFERENCES restaurantes(id)
);

CREATE UNIQUE INDEX uk_categorias_restaurante_nome
    ON categorias (restaurante_id, LOWER(nome));