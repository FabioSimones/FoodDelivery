ALTER TABLE pagamentos
    ADD COLUMN observacao VARCHAR(255);

ALTER TABLE pagamentos
    ADD COLUMN motivo_cancelamento VARCHAR(255);