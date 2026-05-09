package br.com.totem.backend.pagamento.provider;

import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.ResultadoPagamentoSimulado;

import java.math.BigDecimal;

public record PagamentoProviderRequest(
        String numeroPedido,
        FormaPagamento formaPagamento,
        BigDecimal valor,
        ResultadoPagamentoSimulado resultadoSimulado
) {
}