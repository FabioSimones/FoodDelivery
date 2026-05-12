package br.com.totem.backend.cozinha.service;

import br.com.totem.backend.cozinha.dto.CozinhaAtualizacaoStatusResponse;
import br.com.totem.backend.cozinha.dto.CozinhaAtualizarStatusRequest;
import br.com.totem.backend.cozinha.dto.CozinhaItemPedidoResponse;
import br.com.totem.backend.cozinha.dto.CozinhaPedidoResponse;
import br.com.totem.backend.pedido.entity.ItemPedido;
import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.historico.entity.HistoricoStatusPedido;
import br.com.totem.backend.pedido.historico.repository.HistoricoStatusPedidoRepository;
import br.com.totem.backend.pedido.repository.PedidoRepository;
import br.com.totem.backend.restaurante.repository.RestauranteRepository;
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
public class CozinhaService {

    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final HistoricoStatusPedidoRepository historicoStatusPedidoRepository;

    @Transactional(readOnly = true)
    public List<CozinhaPedidoResponse> listarPedidos(UUID restauranteId, StatusPedido statusPedido) {
        List<StatusPedido> statusParaConsulta = definirStatusParaConsulta(statusPedido);

        if (restauranteId != null) {
            validarRestauranteExiste(restauranteId);

            return pedidoRepository.listarFilaCozinhaPorRestaurante(restauranteId, statusParaConsulta)
                    .stream()
                    .map(this::toPedidoResponse)
                    .toList();
        }

        return pedidoRepository.listarFilaCozinha(statusParaConsulta)
                .stream()
                .map(this::toPedidoResponse)
                .toList();
    }

    @Transactional
    public CozinhaAtualizacaoStatusResponse atualizarStatus(
            UUID pedidoId,
            CozinhaAtualizarStatusRequest request
    ) {
        Pedido pedido = buscarPedido(pedidoId);

        StatusPedido statusAnterior = pedido.getStatusPedido();
        StatusPedido statusNovo = request.statusPedido();

        validarStatusPermitidoParaCozinha(statusNovo);
        validarTransicaoStatus(statusAnterior, statusNovo);

        pedido.setStatusPedido(statusNovo);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        registrarHistorico(
                pedidoAtualizado,
                statusAnterior,
                statusNovo,
                normalizarTextoOpcional(request.observacao())
        );

        return new CozinhaAtualizacaoStatusResponse(
                pedidoAtualizado.getId(),
                pedidoAtualizado.getNumeroPedido(),
                statusAnterior,
                statusNovo,
                normalizarTextoOpcional(request.observacao()),
                pedidoAtualizado.getAtualizadoEm()
        );
    }

    private List<StatusPedido> definirStatusParaConsulta(StatusPedido statusPedido) {
        if (statusPedido == null) {
            return List.of(
                    StatusPedido.ENVIADO_PARA_COZINHA,
                    StatusPedido.EM_PREPARO,
                    StatusPedido.PRONTO
            );
        }

        validarStatusListavelNaCozinha(statusPedido);

        return List.of(statusPedido);
    }

    private void validarStatusListavelNaCozinha(StatusPedido statusPedido) {
        List<StatusPedido> statusPermitidos = List.of(
                StatusPedido.ENVIADO_PARA_COZINHA,
                StatusPedido.EM_PREPARO,
                StatusPedido.PRONTO,
                StatusPedido.RETIRADO
        );

        if (!statusPermitidos.contains(statusPedido)) {
            throw new RegraNegocioException(
                    "STATUS_NAO_LISTAVEL_NA_COZINHA",
                    "Este status não faz parte da fila operacional da cozinha."
            );
        }
    }

    private void validarStatusPermitidoParaCozinha(StatusPedido statusNovo) {
        List<StatusPedido> statusPermitidos = List.of(
                StatusPedido.EM_PREPARO,
                StatusPedido.PRONTO,
                StatusPedido.RETIRADO
        );

        if (!statusPermitidos.contains(statusNovo)) {
            throw new RegraNegocioException(
                    "STATUS_NAO_PERMITIDO_NA_COZINHA",
                    "A cozinha só pode alterar pedidos para EM_PREPARO, PRONTO ou RETIRADO."
            );
        }
    }

    private void validarTransicaoStatus(StatusPedido statusAnterior, StatusPedido statusNovo) {
        if (statusAnterior == statusNovo) {
            throw new RegraNegocioException(
                    "STATUS_JA_ATUAL",
                    "O pedido já está com este status."
            );
        }

        boolean transicaoValida =
                statusAnterior == StatusPedido.ENVIADO_PARA_COZINHA && statusNovo == StatusPedido.EM_PREPARO
                        || statusAnterior == StatusPedido.EM_PREPARO && statusNovo == StatusPedido.PRONTO
                        || statusAnterior == StatusPedido.PRONTO && statusNovo == StatusPedido.RETIRADO;

        if (!transicaoValida) {
            throw new RegraNegocioException(
                    "TRANSICAO_STATUS_INVALIDA",
                    "Transição de status inválida para o fluxo da cozinha."
            );
        }
    }

    private Pedido buscarPedido(UUID pedidoId) {
        return pedidoRepository.buscarComItensPorId(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PEDIDO_NAO_ENCONTRADO",
                        "Pedido não encontrado."
                ));
    }

    private void validarRestauranteExiste(UUID restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new RecursoNaoEncontradoException(
                    "RESTAURANTE_NAO_ENCONTRADO",
                    "Restaurante não encontrado."
            );
        }
    }

    private void registrarHistorico(
            Pedido pedido,
            StatusPedido statusAnterior,
            StatusPedido statusNovo,
            String observacao
    ) {
        HistoricoStatusPedido historico = HistoricoStatusPedido.builder()
                .pedido(pedido)
                .statusAnterior(statusAnterior)
                .statusNovo(statusNovo)
                .origem("COZINHA")
                .observacao(observacao)
                .build();

        historicoStatusPedidoRepository.save(historico);
    }

    private CozinhaPedidoResponse toPedidoResponse(Pedido pedido) {
        return new CozinhaPedidoResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getClienteNome(),
                pedido.getTipoConsumo(),
                pedido.getStatusPedido(),
                pedido.getValorTotal(),
                pedido.getCriadoEm(),
                pedido.getItens()
                        .stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private CozinhaItemPedidoResponse toItemResponse(ItemPedido item) {
        return new CozinhaItemPedidoResponse(
                item.getProduto().getId(),
                item.getNomeProduto(),
                item.getQuantidade(),
                item.getObservacao()
        );
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}