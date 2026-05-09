package br.com.totem.backend.pedido.controller;

import br.com.totem.backend.pedido.dto.PedidoCriacaoRequest;
import br.com.totem.backend.pedido.dto.PedidoResponse;
import br.com.totem.backend.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/totem/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@RequestBody @Valid PedidoCriacaoRequest request) {
        return pedidoService.criar(request);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable UUID id) {
        return pedidoService.buscarPorId(id);
    }
}
