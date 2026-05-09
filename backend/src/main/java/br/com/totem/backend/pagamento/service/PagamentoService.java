package br.com.totem.backend.pagamento.service;

import br.com.totem.backend.pagamento.dto.PagamentoIniciarRequest;
import br.com.totem.backend.pagamento.dto.PagamentoResponse;
import br.com.totem.backend.pagamento.entity.Pagamento;
import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pagamento.provider.PagamentoProvider;
import br.com.totem.backend.pagamento.provider.PagamentoProviderRequest;
import br.com.totem.backend.pagamento.provider.PagamentoProviderResponse;
import br.com.totem.backend.pagamento.repository.PagamentoRepository;
import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.repository.PedidoRepository;
import br.com.totem.backend.shared.exception.ConflitoException;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoProvider pagamentoProvider;

    @Transactional
    public PagamentoResponse iniciarPagamento(UUID pedidoId, PagamentoIniciarRequest request) {
        Pedido pedido = buscarPedido(pedidoId);

        validarPedidoPodeIniciarPagamento(pedido);
        validarPagamentoAtivoNaoExiste(pedidoId);

        PagamentoProviderResponse providerResponse = pagamentoProvider.iniciarPagamento(
                new PagamentoProviderRequest(
                        pedido.getNumeroPedido(),
                        request.formaPagamento(),
                        pedido.getValorTotal(),
                        request.resultadoSimulado()
                )
        );

        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .formaPagamento(request.formaPagamento())
                .statusPagamento(providerResponse.statusPagamento())
                .valor(pedido.getValorTotal())
                .paymentProvider(providerResponse.paymentProvider())
                .externalPaymentId(providerResponse.externalPaymentId())
                .qrCodePix(providerResponse.qrCodePix())
                .expiraEm(definirExpiracao(request.formaPagamento(), providerResponse.statusPagamento()))
                .pagoEm(definirDataPagamento(providerResponse.statusPagamento()))
                .build();

        atualizarStatusPedido(pedido, request.formaPagamento(), providerResponse.statusPagamento());

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        pedidoRepository.save(pedido);

        return toResponse(pagamentoSalvo);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscarUltimoPagamentoDoPedido(UUID pedidoId) {
        buscarPedido(pedidoId);

        Pagamento pagamento = pagamentoRepository.findTopByPedido_IdOrderByCriadoEmDesc(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PAGAMENTO_NAO_ENCONTRADO",
                        "Nenhum pagamento foi encontrado para este pedido."
                ));

        return toResponse(pagamento);
    }

    private Pedido buscarPedido(UUID pedidoId) {
        return pedidoRepository.buscarComItensPorId(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PEDIDO_NAO_ENCONTRADO",
                        "Pedido não encontrado."
                ));
    }

    private void validarPedidoPodeIniciarPagamento(Pedido pedido) {
        if (pedido.getStatusPedido() != StatusPedido.CRIADO) {
            throw new RegraNegocioException(
                    "PEDIDO_NAO_PERMITE_PAGAMENTO",
                    "Somente pedidos com status CRIADO podem iniciar pagamento."
            );
        }

        if (pedido.getValorTotal() == null || pedido.getValorTotal().signum() <= 0) {
            throw new RegraNegocioException(
                    "VALOR_PEDIDO_INVALIDO",
                    "O pedido deve possuir valor maior que zero para iniciar pagamento."
            );
        }

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new RegraNegocioException(
                    "PEDIDO_SEM_ITENS",
                    "O pedido deve possuir pelo menos um item para iniciar pagamento."
            );
        }
    }

    private void validarPagamentoAtivoNaoExiste(UUID pedidoId) {
        boolean existePagamentoAtivo = pagamentoRepository.existsByPedido_IdAndStatusPagamentoIn(
                pedidoId,
                List.of(StatusPagamento.PENDENTE, StatusPagamento.AUTORIZADO)
        );

        if (existePagamentoAtivo) {
            throw new ConflitoException(
                    "PAGAMENTO_JA_INICIADO",
                    "Este pedido já possui um pagamento pendente ou autorizado."
            );
        }
    }

    private void atualizarStatusPedido(
            Pedido pedido,
            FormaPagamento formaPagamento,
            StatusPagamento statusPagamento
    ) {
        if (formaPagamento == FormaPagamento.DINHEIRO) {
            pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO_DINHEIRO);
            return;
        }

        switch (statusPagamento) {
            case AUTORIZADO -> pedido.setStatusPedido(StatusPedido.ENVIADO_PARA_COZINHA);
            case PENDENTE -> pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO);
            case RECUSADO -> pedido.setStatusPedido(StatusPedido.CRIADO);
            case CANCELADO, ESTORNADO -> pedido.setStatusPedido(StatusPedido.CRIADO);
        }
    }

    private LocalDateTime definirExpiracao(
            FormaPagamento formaPagamento,
            StatusPagamento statusPagamento
    ) {
        if (formaPagamento == FormaPagamento.PIX && statusPagamento == StatusPagamento.PENDENTE) {
            return LocalDateTime.now().plusMinutes(10);
        }

        return null;
    }

    private LocalDateTime definirDataPagamento(StatusPagamento statusPagamento) {
        if (statusPagamento == StatusPagamento.AUTORIZADO) {
            return LocalDateTime.now();
        }

        return null;
    }

    private PagamentoResponse toResponse(Pagamento pagamento) {
        Pedido pedido = pagamento.getPedido();

        return new PagamentoResponse(
                pagamento.getId(),
                pedido.getId(),
                pedido.getNumeroPedido(),
                pagamento.getFormaPagamento(),
                pagamento.getStatusPagamento(),
                pagamento.getValor(),
                pagamento.getPaymentProvider(),
                pagamento.getExternalPaymentId(),
                pagamento.getQrCodePix(),
                pagamento.getExpiraEm(),
                pedido.getStatusPedido(),
                pagamento.getCriadoEm(),
                pagamento.getPagoEm(),
                pagamento.getCanceladoEm()
        );
    }
}
