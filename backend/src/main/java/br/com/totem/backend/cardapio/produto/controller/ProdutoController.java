package br.com.totem.backend.cardapio.produto.controller;
import br.com.totem.backend.cardapio.produto.dto.ProdutoAtualizacaoRequest;
import br.com.totem.backend.cardapio.produto.dto.ProdutoBooleanRequest;
import br.com.totem.backend.cardapio.produto.dto.ProdutoCriacaoRequest;
import br.com.totem.backend.cardapio.produto.dto.ProdutoResponse;
import br.com.totem.backend.cardapio.produto.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@RequestBody @Valid ProdutoCriacaoRequest request) {
        return produtoService.criar(request);
    }

    @GetMapping
    public List<ProdutoResponse> listar(
            @RequestParam(required = false) UUID restauranteId,
            @RequestParam(required = false) UUID categoriaId
    ) {
        return produtoService.listar(restauranteId, categoriaId);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscarPorId(@PathVariable UUID id) {
        return produtoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid ProdutoAtualizacaoRequest request
    ) {
        return produtoService.atualizar(id, request);
    }

    @PatchMapping("/{id}/disponibilidade")
    public ProdutoResponse alterarDisponibilidade(
            @PathVariable UUID id,
            @RequestBody @Valid ProdutoBooleanRequest request
    ) {
        return produtoService.alterarDisponibilidade(id, request.valor());
    }

    @PatchMapping("/{id}/destaque")
    public ProdutoResponse alterarDestaque(
            @PathVariable UUID id,
            @RequestBody @Valid ProdutoBooleanRequest request
    ) {
        return produtoService.alterarDestaque(id, request.valor());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        produtoService.desativar(id);
    }
}
