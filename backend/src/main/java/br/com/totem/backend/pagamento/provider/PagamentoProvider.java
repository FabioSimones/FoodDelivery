package br.com.totem.backend.pagamento.provider;

public interface PagamentoProvider {
    PagamentoProviderResponse iniciarPagamento(PagamentoProviderRequest request);
}