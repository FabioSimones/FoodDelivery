package br.com.totem.backend.caixa.service;

import br.com.totem.backend.caixa.dto.CaixaCancelarPedidoRequest;
import br.com.totem.backend.caixa.dto.CaixaConfirmarPagamentoRequest;
import br.com.totem.backend.caixa.dto.CaixaOperacaoPedidoResponse;
import br.com.totem.backend.caixa.dto.CaixaPedidoPendenteResponse;
import br.com.totem.backend.pagamento.entity.Pagamento;
import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pagamento.repository.PagamentoRepository;
import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.repository.PedidoRepository;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaixaService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<CaixaPedidoPendenteResponse> listarPedidosPendentes() {
        return pagamentoRepository.listarPagamentosPendentesCaixa(
                        FormaPagamento.DINHEIRO,
                        StatusPagamento.PENDENTE,
                        StatusPedido.AGUARDANDO_PAGAMENTO_DINHEIRO
                )
                .stream()
                .map(this::toPendenteResponse)
                .toList();
    }

    @Transactional
    public CaixaOperacaoPedidoResponse confirmarPagamento(
            UUID pedidoId,
            CaixaConfirmarPagamentoRequest request
    ) {
        Pedido pedido = buscarPedido(pedidoId);

        validarPedidoAguardandoPagamentoDinheiro(pedido);

        Pagamento pagamento = buscarPagamentoDinheiroPendente(pedidoId);

        if (request.valorRecebido().compareTo(pedido.getValorTotal()) < 0) {
            throw new RegraNegocioException(
                    "VALOR_RECEBIDO_INSUFICIENTE",
                    "O valor recebido não pode ser menor que o valor total do pedido."
            );
        }

        BigDecimal troco = request.valorRecebido()
                .subtract(pedido.getValorTotal())
                .setScale(2, RoundingMode.HALF_UP);

        LocalDateTime agora = LocalDateTime.now();

        pagamento.setStatusPagamento(StatusPagamento.AUTORIZADO);
        pagamento.setPagoEm(agora);
        pagamento.setObservacao(normalizarTextoOpcional(request.observacao()));

        pedido.setStatusPedido(StatusPedido.ENVIADO_PARA_COZINHA);

        pagamentoRepository.save(pagamento);
        pedidoRepository.save(pedido);

        return toOperacaoResponse(
                pagamento,
                request.valorRecebido(),
                troco
        );
    }

    @Transactional
    public CaixaOperacaoPedidoResponse cancelarPedido(
            UUID pedidoId,
            CaixaCancelarPedidoRequest request
    ) {
        Pedido pedido = buscarPedido(pedidoId);

        validarPedidoAguardandoPagamentoDinheiro(pedido);

        Pagamento pagamento = buscarPagamentoDinheiroPendente(pedidoId);

        LocalDateTime agora = LocalDateTime.now();

        pagamento.setStatusPagamento(StatusPagamento.CANCELADO);
        pagamento.setCanceladoEm(agora);
        pagamento.setMotivoCancelamento(normalizarTextoObrigatorio(
                request.motivo(),
                "O motivo do cancelamento é obrigatório."
        ));

        pedido.setStatusPedido(StatusPedido.CANCELADO);

        pagamentoRepository.save(pagamento);
        pedidoRepository.save(pedido);

        return toOperacaoResponse(
                pagamento,
                null,
                null
        );
    }

    private Pedido buscarPedido(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PEDIDO_NAO_ENCONTRADO",
                        "Pedido não encontrado."
                ));
    }

    private Pagamento buscarPagamentoDinheiroPendente(UUID pedidoId) {
        return pagamentoRepository
                .findTopByPedido_IdAndFormaPagamentoAndStatusPagamentoOrderByCriadoEmDesc(
                        pedidoId,
                        FormaPagamento.DINHEIRO,
                        StatusPagamento.PENDENTE
                )
                .orElseThrow(() -> new RegraNegocioException(
                        "PAGAMENTO_DINHEIRO_PENDENTE_NAO_ENCONTRADO",
                        "Não existe pagamento em dinheiro pendente para este pedido."
                ));
    }

    private void validarPedidoAguardandoPagamentoDinheiro(Pedido pedido) {
        if (pedido.getStatusPedido() != StatusPedido.AGUARDANDO_PAGAMENTO_DINHEIRO) {
            throw new RegraNegocioException(
                    "PEDIDO_NAO_AGUARDA_DINHEIRO",
                    "Somente pedidos aguardando pagamento em dinheiro podem ser processados pelo caixa."
            );
        }
    }

    private CaixaPedidoPendenteResponse toPendenteResponse(Pagamento pagamento) {
        Pedido pedido = pagamento.getPedido();

        return new CaixaPedidoPendenteResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getClienteNome(),
                pedido.getTipoConsumo(),
                pedido.getValorTotal(),
                pagamento.getFormaPagamento(),
                pagamento.getStatusPagamento(),
                pedido.getStatusPedido(),
                pedido.getCriadoEm()
        );
    }

    private CaixaOperacaoPedidoResponse toOperacaoResponse(
            Pagamento pagamento,
            BigDecimal valorRecebido,
            BigDecimal troco
    ) {
        Pedido pedido = pagamento.getPedido();

        return new CaixaOperacaoPedidoResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pagamento.getId(),
                pagamento.getFormaPagamento(),
                pagamento.getStatusPagamento(),
                pedido.getStatusPedido(),
                pedido.getValorTotal(),
                valorRecebido,
                troco,
                pagamento.getObservacao(),
                pagamento.getMotivoCancelamento(),
                pagamento.getPagoEm(),
                pagamento.getCanceladoEm()
        );
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    private String normalizarTextoObrigatorio(String texto, String mensagemErro) {
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException(
                    "TEXTO_OBRIGATORIO",
                    mensagemErro
            );
        }

        return texto.trim();
    }
}
