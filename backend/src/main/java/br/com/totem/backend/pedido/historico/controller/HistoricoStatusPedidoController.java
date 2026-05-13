package br.com.totem.backend.pedido.historico.controller;

import br.com.totem.backend.pedido.historico.dto.HistoricoStatusPedidoResponse;
import br.com.totem.backend.pedido.historico.service.HistoricoStatusPedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos/{pedidoId}/historico-status")
@RequiredArgsConstructor
public class HistoricoStatusPedidoController {

    private final HistoricoStatusPedidoService historicoStatusPedidoService;

    @GetMapping
    public List<HistoricoStatusPedidoResponse> listarPorPedido(
            @PathVariable UUID pedidoId
    ) {
        return historicoStatusPedidoService.listarPorPedido(pedidoId);
    }
}