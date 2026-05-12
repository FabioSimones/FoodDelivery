package br.com.totem.backend.cozinha.controller;

import br.com.totem.backend.cozinha.dto.CozinhaAtualizacaoStatusResponse;
import br.com.totem.backend.cozinha.dto.CozinhaAtualizarStatusRequest;
import br.com.totem.backend.cozinha.dto.CozinhaPedidoResponse;
import br.com.totem.backend.cozinha.service.CozinhaService;
import br.com.totem.backend.pedido.enums.StatusPedido;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cozinha/pedidos")
@RequiredArgsConstructor
public class CozinhaController {

    private final CozinhaService cozinhaService;

    @GetMapping
    public List<CozinhaPedidoResponse> listarPedidos(
            @RequestParam(required = false) UUID restauranteId,
            @RequestParam(required = false) StatusPedido statusPedido
    ) {
        return cozinhaService.listarPedidos(restauranteId, statusPedido);
    }

    @PatchMapping("/{id}/status")
    public CozinhaAtualizacaoStatusResponse atualizarStatus(
            @PathVariable UUID id,
            @RequestBody @Valid CozinhaAtualizarStatusRequest request
    ) {
        return cozinhaService.atualizarStatus(id, request);
    }
}