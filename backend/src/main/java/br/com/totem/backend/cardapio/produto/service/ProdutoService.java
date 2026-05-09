package br.com.totem.backend.cardapio.produto.service;

import br.com.totem.backend.cardapio.produto.dto.ProdutoAtualizacaoRequest;
import br.com.totem.backend.cardapio.produto.dto.ProdutoCriacaoRequest;
import br.com.totem.backend.cardapio.produto.dto.ProdutoResponse;
import br.com.totem.backend.cardapio.produto.entity.Produto;
import br.com.totem.backend.cardapio.produto.repository.ProdutoRepository;
import br.com.totem.backend.categoria.entity.Categoria;
import br.com.totem.backend.categoria.repository.CategoriaRepository;
import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.restaurante.repository.RestauranteRepository;
import br.com.totem.backend.shared.exception.ConflitoException;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public ProdutoResponse criar(ProdutoCriacaoRequest request) {
        Restaurante restaurante = buscarRestaurante(request.restauranteId());
        Categoria categoria = buscarCategoria(request.categoriaId());

        validarCategoriaPertenceAoRestaurante(categoria, restaurante.getId());

        String nomeNormalizado = normalizarNome(request.nome());
        validarPreco(request.preco());

        if (produtoRepository.existsByRestaurante_IdAndNomeIgnoreCase(
                restaurante.getId(),
                nomeNormalizado
        )) {
            throw new ConflitoException(
                    "PRODUTO_DUPLICADO",
                    "Já existe um produto cadastrado com este nome para este restaurante."
            );
        }

        Produto produto = Produto.builder()
                .restaurante(restaurante)
                .categoria(categoria)
                .nome(nomeNormalizado)
                .descricao(normalizarTextoOpcional(request.descricao()))
                .preco(request.preco())
                .imagemUrl(normalizarTextoOpcional(request.imagemUrl()))
                .disponivel(request.disponivel())
                .destaque(request.destaque())
                .recomendado(request.recomendado())
                .ordemExibicao(request.ordemExibicao())
                .build();

        Produto produtoSalvo = produtoRepository.save(produto);

        return toResponse(produtoSalvo);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar(UUID restauranteId, UUID categoriaId) {
        if (restauranteId != null) {
            buscarRestaurante(restauranteId);

            return produtoRepository.listarPorRestauranteOrdenados(restauranteId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (categoriaId != null) {
            buscarCategoria(categoriaId);

            return produtoRepository.listarPorCategoriaOrdenados(categoriaId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return produtoRepository.listarTodosOrdenados()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(UUID id) {
        Produto produto = buscarProduto(id);
        return toResponse(produto);
    }

    @Transactional
    public ProdutoResponse atualizar(UUID id, ProdutoAtualizacaoRequest request) {
        Produto produto = buscarProduto(id);
        Categoria categoria = buscarCategoria(request.categoriaId());

        UUID restauranteId = produto.getRestaurante().getId();

        validarCategoriaPertenceAoRestaurante(categoria, restauranteId);

        String nomeNormalizado = normalizarNome(request.nome());
        validarPreco(request.preco());

        if (produtoRepository.existsByRestaurante_IdAndNomeIgnoreCaseAndIdNot(
                restauranteId,
                nomeNormalizado,
                id
        )) {
            throw new ConflitoException(
                    "PRODUTO_DUPLICADO",
                    "Já existe outro produto cadastrado com este nome para este restaurante."
            );
        }

        produto.setCategoria(categoria);
        produto.setNome(nomeNormalizado);
        produto.setDescricao(normalizarTextoOpcional(request.descricao()));
        produto.setPreco(request.preco());
        produto.setImagemUrl(normalizarTextoOpcional(request.imagemUrl()));

        if (request.disponivel() != null) {
            produto.setDisponivel(request.disponivel());
        }

        if (request.destaque() != null) {
            produto.setDestaque(request.destaque());
        }

        if (request.recomendado() != null) {
            produto.setRecomendado(request.recomendado());
        }

        produto.setOrdemExibicao(request.ordemExibicao());

        Produto produtoAtualizado = produtoRepository.save(produto);

        return toResponse(produtoAtualizado);
    }

    @Transactional
    public ProdutoResponse alterarDisponibilidade(UUID id, Boolean disponivel) {
        Produto produto = buscarProduto(id);

        if (disponivel == null) {
            throw new RegraNegocioException(
                    "DISPONIBILIDADE_INVALIDA",
                    "A disponibilidade do produto deve ser informada."
            );
        }

        produto.setDisponivel(disponivel);

        Produto produtoAtualizado = produtoRepository.save(produto);

        return toResponse(produtoAtualizado);
    }

    @Transactional
    public ProdutoResponse alterarDestaque(UUID id, Boolean destaque) {
        Produto produto = buscarProduto(id);

        if (destaque == null) {
            throw new RegraNegocioException(
                    "DESTAQUE_INVALIDO",
                    "O destaque do produto deve ser informado."
            );
        }

        produto.setDestaque(destaque);

        Produto produtoAtualizado = produtoRepository.save(produto);

        return toResponse(produtoAtualizado);
    }

    @Transactional
    public void desativar(UUID id) {
        Produto produto = buscarProduto(id);

        if (Boolean.FALSE.equals(produto.getDisponivel())) {
            throw new RegraNegocioException(
                    "PRODUTO_JA_INDISPONIVEL",
                    "Este produto já está indisponível."
            );
        }

        produto.setDisponivel(false);
        produtoRepository.save(produto);
    }

    private Restaurante buscarRestaurante(UUID restauranteId) {
        return restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "RESTAURANTE_NAO_ENCONTRADO",
                        "Restaurante não encontrado."
                ));
    }

    private Categoria buscarCategoria(UUID categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "CATEGORIA_NAO_ENCONTRADA",
                        "Categoria não encontrada."
                ));
    }

    private Produto buscarProduto(UUID produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PRODUTO_NAO_ENCONTRADO",
                        "Produto não encontrado."
                ));
    }

    private void validarCategoriaPertenceAoRestaurante(Categoria categoria, UUID restauranteId) {
        if (!categoria.getRestaurante().getId().equals(restauranteId)) {
            throw new RegraNegocioException(
                    "CATEGORIA_NAO_PERTENCE_AO_RESTAURANTE",
                    "A categoria informada não pertence ao restaurante do produto."
            );
        }
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException(
                    "NOME_PRODUTO_INVALIDO",
                    "O nome do produto é obrigatório."
            );
        }

        return nome.trim();
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(
                    "PRECO_PRODUTO_INVALIDO",
                    "O preço do produto deve ser maior que zero."
            );
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getRestaurante().getId(),
                produto.getRestaurante().getNome(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getImagemUrl(),
                produto.getDisponivel(),
                produto.getDestaque(),
                produto.getRecomendado(),
                produto.getOrdemExibicao(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }
}
