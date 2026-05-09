package br.com.totem.backend.cardapio.service;

import br.com.totem.backend.cardapio.dto.CardapioTotemResponse;
import br.com.totem.backend.cardapio.dto.CategoriaCardapioTotemResponse;
import br.com.totem.backend.cardapio.dto.ProdutoCardapioTotemResponse;
import br.com.totem.backend.cardapio.produto.entity.Produto;
import br.com.totem.backend.cardapio.produto.repository.ProdutoRepository;
import br.com.totem.backend.categoria.entity.Categoria;
import br.com.totem.backend.categoria.repository.CategoriaRepository;
import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.restaurante.repository.RestauranteRepository;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardapioTotemService {

    private final RestauranteRepository restauranteRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public CardapioTotemResponse buscarCardapio(UUID restauranteId) {
        Restaurante restaurante = buscarRestaurante(restauranteId);

        if (!Boolean.TRUE.equals(restaurante.getAtivo())) {
            throw new RegraNegocioException(
                    "RESTAURANTE_INATIVO",
                    "Este restaurante não está ativo para exibição do cardápio."
            );
        }

        List<Categoria> categorias = categoriaRepository
                .listarAtivasPorRestauranteOrdenadas(restauranteId);

        List<Produto> produtosDisponiveis = produtoRepository
                .listarDisponiveisParaTotem(restauranteId);

        Map<UUID, List<ProdutoCardapioTotemResponse>> produtosPorCategoria =
                produtosDisponiveis.stream()
                        .collect(Collectors.groupingBy(
                                produto -> produto.getCategoria().getId(),
                                LinkedHashMap::new,
                                Collectors.mapping(this::toProdutoResponse, Collectors.toList())
                        ));

        List<CategoriaCardapioTotemResponse> categoriasResponse = categorias.stream()
                .map(categoria -> new CategoriaCardapioTotemResponse(
                        categoria.getId(),
                        categoria.getNome(),
                        categoria.getDescricao(),
                        categoria.getOrdemExibicao(),
                        produtosPorCategoria.getOrDefault(categoria.getId(), List.of())
                ))
                .filter(categoria -> !categoria.produtos().isEmpty())
                .toList();

        List<ProdutoCardapioTotemResponse> destaques = produtosDisponiveis.stream()
                .filter(produto -> Boolean.TRUE.equals(produto.getDestaque()))
                .map(this::toProdutoResponse)
                .toList();

        List<ProdutoCardapioTotemResponse> recomendados = produtosDisponiveis.stream()
                .filter(produto -> Boolean.TRUE.equals(produto.getRecomendado()))
                .map(this::toProdutoResponse)
                .toList();

        return new CardapioTotemResponse(
                restaurante.getId(),
                restaurante.getNome(),
                categoriasResponse,
                destaques,
                recomendados
        );
    }

    private Restaurante buscarRestaurante(UUID restauranteId) {
        return restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "RESTAURANTE_NAO_ENCONTRADO",
                        "Restaurante não encontrado."
                ));
    }

    private ProdutoCardapioTotemResponse toProdutoResponse(Produto produto) {
        return new ProdutoCardapioTotemResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getImagemUrl(),
                produto.getDestaque(),
                produto.getRecomendado(),
                produto.getOrdemExibicao()
        );
    }
}