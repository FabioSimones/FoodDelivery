CREATE TABLE pagamentos (
                            id UUID PRIMARY KEY,
                            pedido_id UUID NOT NULL,
                            forma_pagamento VARCHAR(40) NOT NULL,
                            status_pagamento VARCHAR(40) NOT NULL,
                            valor NUMERIC(10, 2) NOT NULL,
                            payment_provider VARCHAR(80) NOT NULL,
                            external_payment_id VARCHAR(120),
                            qr_code_pix TEXT,
                            expira_em TIMESTAMP,
                            criado_em TIMESTAMP NOT NULL,
                            pago_em TIMESTAMP,
                            cancelado_em TIMESTAMP,

                            CONSTRAINT fk_pagamentos_pedidos
                                FOREIGN KEY (pedido_id)
                                    REFERENCES pedidos(id),

                            CONSTRAINT ck_pagamentos_valor
                                CHECK (valor > 0)
);

CREATE INDEX idx_pagamentos_pedido_id
    ON pagamentos (pedido_id);

CREATE INDEX idx_pagamentos_status
    ON pagamentos (status_pagamento);