package br.com.totem.backend.pedido.historico.service;

import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.historico.dto.HistoricoStatusPedidoResponse;
import br.com.totem.backend.pedido.historico.entity.HistoricoStatusPedido;
import br.com.totem.backend.pedido.historico.repository.HistoricoStatusPedidoRepository;
import br.com.totem.backend.pedido.repository.PedidoRepository;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoStatusPedidoService {

    private final HistoricoStatusPedidoRepository historicoStatusPedidoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public void registrar(
            Pedido pedido,
            StatusPedido statusAnterior,
            StatusPedido statusNovo,
            String origem,
            String observacao
    ) {
        HistoricoStatusPedido historico = HistoricoStatusPedido.builder()
                .pedido(pedido)
                .statusAnterior(statusAnterior)
                .statusNovo(statusNovo)
                .origem(origem)
                .observacao(normalizarTextoOpcional(observacao))
                .build();

        historicoStatusPedidoRepository.save(historico);
    }

    @Transactional(readOnly = true)
    public List<HistoricoStatusPedidoResponse> listarPorPedido(UUID pedidoId) {
        validarPedidoExiste(pedidoId);

        return historicoStatusPedidoRepository.listarPorPedidoOrdenado(pedidoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validarPedidoExiste(UUID pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new RecursoNaoEncontradoException(
                    "PEDIDO_NAO_ENCONTRADO",
                    "Pedido não encontrado."
            );
        }
    }

    private HistoricoStatusPedidoResponse toResponse(HistoricoStatusPedido historico) {
        Pedido pedido = historico.getPedido();

        return new HistoricoStatusPedidoResponse(
                historico.getId(),
                pedido.getId(),
                pedido.getNumeroPedido(),
                historico.getStatusAnterior(),
                historico.getStatusNovo(),
                historico.getOrigem(),
                historico.getObservacao(),
                historico.getDataAlteracao()
        );
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}