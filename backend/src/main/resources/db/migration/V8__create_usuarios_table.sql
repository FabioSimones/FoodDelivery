CREATE TABLE usuarios
(
    id             UUID PRIMARY KEY,
    restaurante_id UUID,
    nome           VARCHAR(150) NOT NULL,
    email          VARCHAR(160) NOT NULL,
    senha_hash     VARCHAR(255) NOT NULL,
    perfil         VARCHAR(50)  NOT NULL,
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMP    NOT NULL,
    atualizado_em  TIMESTAMP,

    CONSTRAINT fk_usuarios_restaurantes
        FOREIGN KEY (restaurante_id)
            REFERENCES restaurantes (id)
);

CREATE UNIQUE INDEX uk_usuarios_email_lower
    ON usuarios (LOWER(email));

CREATE INDEX idx_usuarios_restaurante_id
    ON usuarios (restaurante_id);