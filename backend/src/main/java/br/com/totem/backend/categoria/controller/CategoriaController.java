package br.com.totem.backend.categoria.controller;

import br.com.totem.backend.categoria.dto.CategoriaAtualizacaoRequest;
import br.com.totem.backend.categoria.dto.CategoriaCriacaoRequest;
import br.com.totem.backend.categoria.dto.CategoriaResponse;
import br.com.totem.backend.categoria.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse criar(@RequestBody @Valid CategoriaCriacaoRequest request) {
        return categoriaService.criar(request);
    }

    @GetMapping
    public List<CategoriaResponse> listar(
            @RequestParam(required = false) UUID restauranteId
    ) {
        return categoriaService.listar(restauranteId);
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(@PathVariable UUID id) {
        return categoriaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid CategoriaAtualizacaoRequest request
    ) {
        return categoriaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        categoriaService.desativar(id);
    }
}
