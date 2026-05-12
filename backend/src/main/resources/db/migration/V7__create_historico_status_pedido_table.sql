CREATE TABLE historico_status_pedido
(
    id                          UUID PRIMARY KEY,
    pedido_id                   UUID        NOT NULL,
    status_anterior             VARCHAR(50),
    status_novo                 VARCHAR(50) NOT NULL,
    data_alteracao              TIMESTAMP   NOT NULL,
    alterado_por_usuario_id     UUID,
    alterado_por_dispositivo_id UUID,
    origem                      VARCHAR(50),
    observacao                  VARCHAR(255),

    CONSTRAINT fk_historico_status_pedido_pedidos
        FOREIGN KEY (pedido_id)
            REFERENCES pedidos (id)
);

CREATE INDEX idx_historico_status_pedido_pedido_id
    ON historico_status_pedido (pedido_id);