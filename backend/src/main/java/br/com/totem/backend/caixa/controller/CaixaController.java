package br.com.totem.backend.caixa.controller;

import br.com.totem.backend.caixa.dto.CaixaCancelarPedidoRequest;
import br.com.totem.backend.caixa.dto.CaixaConfirmarPagamentoRequest;
import br.com.totem.backend.caixa.dto.CaixaOperacaoPedidoResponse;
import br.com.totem.backend.caixa.dto.CaixaPedidoPendenteResponse;
import br.com.totem.backend.caixa.service.CaixaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/caixa/pedidos")
@RequiredArgsConstructor
public class CaixaController {

    private final CaixaService caixaService;

    @GetMapping("/pendentes")
    public List<CaixaPedidoPendenteResponse> listarPedidosPendentes() {
        return caixaService.listarPedidosPendentes();
    }

    @PostMapping("/{id}/confirmar-pagamento")
    public CaixaOperacaoPedidoResponse confirmarPagamento(
            @PathVariable UUID id,
            @RequestBody @Valid CaixaConfirmarPagamentoRequest request
    ) {
        return caixaService.confirmarPagamento(id, request);
    }

    @PostMapping("/{id}/cancelar")
    public CaixaOperacaoPedidoResponse cancelarPedido(
            @PathVariable UUID id,
            @RequestBody @Valid CaixaCancelarPedidoRequest request
    ) {
        return caixaService.cancelarPedido(id, request);
    }
}
