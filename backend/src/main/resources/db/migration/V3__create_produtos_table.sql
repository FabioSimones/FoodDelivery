CREATE TABLE produtos (
                          id UUID PRIMARY KEY,
                          restaurante_id UUID NOT NULL,
                          categoria_id UUID NOT NULL,
                          nome VARCHAR(150) NOT NULL,
                          descricao VARCHAR(500),
                          preco NUMERIC(10, 2) NOT NULL,
                          imagem_url VARCHAR(500),
                          disponivel BOOLEAN NOT NULL DEFAULT TRUE,
                          destaque BOOLEAN NOT NULL DEFAULT FALSE,
                          recomendado BOOLEAN NOT NULL DEFAULT FALSE,
                          ordem_exibicao INTEGER NOT NULL DEFAULT 0,
                          criado_em TIMESTAMP NOT NULL,
                          atualizado_em TIMESTAMP,

                          CONSTRAINT fk_produtos_restaurantes
                              FOREIGN KEY (restaurante_id)
                                  REFERENCES restaurantes(id),

                          CONSTRAINT fk_produtos_categorias
                              FOREIGN KEY (categoria_id)
                                  REFERENCES categorias(id),

                          CONSTRAINT ck_produtos_preco_positivo
                              CHECK (preco > 0)
);

CREATE UNIQUE INDEX uk_produtos_restaurante_nome
    ON produtos (restaurante_id, LOWER(nome));