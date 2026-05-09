package br.com.totem.backend.pagamento.provider;

import br.com.totem.backend.pagamento.enums.StatusPagamento;

public record PagamentoProviderResponse(
        StatusPagamento statusPagamento,
        String paymentProvider,
        String externalPaymentId,
        String qrCodePix
) {
}