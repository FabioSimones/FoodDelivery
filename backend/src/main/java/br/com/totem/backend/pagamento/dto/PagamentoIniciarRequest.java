package br.com.totem.backend.pagamento.dto;

import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.ResultadoPagamentoSimulado;
import jakarta.validation.constraints.NotNull;

public record PagamentoIniciarRequest(

        @NotNull(message = "A forma de pagamento é obrigatória.")
        FormaPagamento formaPagamento,

        ResultadoPagamentoSimulado resultadoSimulado
) {
}