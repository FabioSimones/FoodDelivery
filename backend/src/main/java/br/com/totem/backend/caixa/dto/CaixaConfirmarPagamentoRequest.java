package br.com.totem.backend.caixa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CaixaConfirmarPagamentoRequest(

        @NotNull(message = "O valor recebido é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor recebido deve ser maior que zero.")
        BigDecimal valorRecebido,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres.")
        String observacao
) {
}
