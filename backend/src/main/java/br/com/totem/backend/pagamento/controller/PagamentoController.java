package br.com.totem.backend.pagamento.controller;

import br.com.totem.backend.pagamento.dto.PagamentoIniciarRequest;
import br.com.totem.backend.pagamento.dto.PagamentoResponse;
import br.com.totem.backend.pagamento.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/totem/pedidos/{pedidoId}/pagamento")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    public PagamentoResponse iniciarPagamento(
            @PathVariable UUID pedidoId,
            @RequestBody @Valid PagamentoIniciarRequest request
    ) {
        return pagamentoService.iniciarPagamento(pedidoId, request);
    }

    @GetMapping
    public PagamentoResponse buscarUltimoPagamento(
            @PathVariable UUID pedidoId
    ) {
        return pagamentoService.buscarUltimoPagamentoDoPedido(pedidoId);
    }
}
