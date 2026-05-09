package br.com.totem.backend.pagamento.provider;

import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.ResultadoPagamentoSimulado;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FakePaymentProvider implements PagamentoProvider {

    @Override
    public PagamentoProviderResponse iniciarPagamento(PagamentoProviderRequest request) {
        if (request.formaPagamento() == FormaPagamento.DINHEIRO) {
            return new PagamentoProviderResponse(
                    StatusPagamento.PENDENTE,
                    "DINHEIRO_MANUAL",
                    null,
                    null
            );
        }

        StatusPagamento status = definirStatusSimulado(request.resultadoSimulado());

        String externalPaymentId = "FAKE-" + UUID.randomUUID();

        String qrCodePix = null;

        if (request.formaPagamento() == FormaPagamento.PIX) {
            qrCodePix = "000201FAKEPIX-" + request.numeroPedido() + "-" + externalPaymentId;
        }

        return new PagamentoProviderResponse(
                status,
                "FAKE_PAYMENT_PROVIDER",
                externalPaymentId,
                qrCodePix
        );
    }

    private StatusPagamento definirStatusSimulado(ResultadoPagamentoSimulado resultadoSimulado) {
        ResultadoPagamentoSimulado resultado = resultadoSimulado == null
                ? ResultadoPagamentoSimulado.APROVAR
                : resultadoSimulado;

        return switch (resultado) {
            case APROVAR -> StatusPagamento.AUTORIZADO;
            case RECUSAR -> StatusPagamento.RECUSADO;
            case PENDENTE -> StatusPagamento.PENDENTE;
        };
    }
}