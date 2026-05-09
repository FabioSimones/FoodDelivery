CREATE SEQUENCE pedidos_numero_seq
    START WITH 1000
    INCREMENT BY 1;

CREATE TABLE pedidos (
                         id UUID PRIMARY KEY,
                         restaurante_id UUID NOT NULL,
                         numero_pedido VARCHAR(30) NOT NULL,
                         cliente_nome VARCHAR(100),
                         tipo_consumo VARCHAR(30) NOT NULL,
                         status_pedido VARCHAR(50) NOT NULL,
                         valor_total NUMERIC(10, 2) NOT NULL,
                         dispositivo_origem_id UUID,
                         criado_em TIMESTAMP NOT NULL,
                         atualizado_em TIMESTAMP,

                         CONSTRAINT fk_pedidos_restaurantes
                             FOREIGN KEY (restaurante_id)
                                 REFERENCES restaurantes(id),

                         CONSTRAINT uk_pedidos_numero_pedido
                             UNIQUE (numero_pedido),

                         CONSTRAINT ck_pedidos_valor_total
                             CHECK (valor_total >= 0)
);

CREATE TABLE itens_pedido (
                              id UUID PRIMARY KEY,
                              pedido_id UUID NOT NULL,
                              produto_id UUID NOT NULL,
                              nome_produto VARCHAR(150) NOT NULL,
                              quantidade INTEGER NOT NULL,
                              preco_unitario NUMERIC(10, 2) NOT NULL,
                              subtotal NUMERIC(10, 2) NOT NULL,
                              observacao VARCHAR(255),

                              CONSTRAINT fk_itens_pedido_pedidos
                                  FOREIGN KEY (pedido_id)
                                      REFERENCES pedidos(id),

                              CONSTRAINT fk_itens_pedido_produtos
                                  FOREIGN KEY (produto_id)
                                      REFERENCES produtos(id),

                              CONSTRAINT ck_itens_pedido_quantidade
                                  CHECK (quantidade > 0),

                              CONSTRAINT ck_itens_pedido_preco_unitario
                                  CHECK (preco_unitario > 0),

                              CONSTRAINT ck_itens_pedido_subtotal
                                  CHECK (subtotal > 0)
);

CREATE INDEX idx_pedidos_restaurante_id
    ON pedidos (restaurante_id);

CREATE INDEX idx_itens_pedido_pedido_id
    ON itens_pedido (pedido_id);