package br.com.totem.backend.pedido.service;

import br.com.totem.backend.cardapio.produto.entity.Produto;
import br.com.totem.backend.cardapio.produto.repository.ProdutoRepository;
import br.com.totem.backend.pedido.dto.ItemPedidoCriacaoRequest;
import br.com.totem.backend.pedido.dto.ItemPedidoResponse;
import br.com.totem.backend.pedido.dto.PedidoCriacaoRequest;
import br.com.totem.backend.pedido.dto.PedidoResponse;
import br.com.totem.backend.pedido.entity.ItemPedido;
import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.repository.PedidoRepository;
import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.restaurante.repository.RestauranteRepository;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public PedidoResponse criar(PedidoCriacaoRequest request) {
        Restaurante restaurante = buscarRestaurante(request.restauranteId());

        if (!Boolean.TRUE.equals(restaurante.getAtivo())) {
            throw new RegraNegocioException(
                    "RESTAURANTE_INATIVO",
                    "Não é possível criar pedido para um restaurante inativo."
            );
        }

        validarItensDuplicados(request.itens());

        Pedido pedido = Pedido.builder()
                .restaurante(restaurante)
                .numeroPedido(gerarNumeroPedido())
                .clienteNome(normalizarTextoOpcional(request.clienteNome()))
                .tipoConsumo(request.tipoConsumo())
                .statusPedido(StatusPedido.CRIADO)
                .valorTotal(BigDecimal.ZERO)
                .build();

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedidoCriacaoRequest itemRequest : request.itens()) {
            Produto produto = buscarProduto(itemRequest.produtoId());

            validarProdutoParaPedido(produto, restaurante.getId());

            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal subtotal = precoUnitario
                    .multiply(BigDecimal.valueOf(itemRequest.quantidade()))
                    .setScale(2, RoundingMode.HALF_UP);

            ItemPedido itemPedido = ItemPedido.builder()
                    .produto(produto)
                    .nomeProduto(produto.getNome())
                    .quantidade(itemRequest.quantidade())
                    .precoUnitario(precoUnitario)
                    .subtotal(subtotal)
                    .observacao(normalizarTextoOpcional(itemRequest.observacao()))
                    .build();

            pedido.adicionarItem(itemPedido);

            valorTotal = valorTotal.add(subtotal);
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(
                    "VALOR_PEDIDO_INVALIDO",
                    "O valor total do pedido deve ser maior que zero."
            );
        }

        pedido.setValorTotal(valorTotal.setScale(2, RoundingMode.HALF_UP));

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return toResponse(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(UUID id) {
        Pedido pedido = pedidoRepository.buscarComItensPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PEDIDO_NAO_ENCONTRADO",
                        "Pedido não encontrado."
                ));

        return toResponse(pedido);
    }

    private Restaurante buscarRestaurante(UUID restauranteId) {
        return restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "RESTAURANTE_NAO_ENCONTRADO",
                        "Restaurante não encontrado."
                ));
    }

    private Produto buscarProduto(UUID produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "PRODUTO_NAO_ENCONTRADO",
                        "Produto não encontrado."
                ));
    }

    private void validarProdutoParaPedido(Produto produto, UUID restauranteId) {
        if (!produto.getRestaurante().getId().equals(restauranteId)) {
            throw new RegraNegocioException(
                    "PRODUTO_NAO_PERTENCE_AO_RESTAURANTE",
                    "O produto informado não pertence ao restaurante do pedido."
            );
        }

        if (!Boolean.TRUE.equals(produto.getDisponivel())) {
            throw new RegraNegocioException(
                    "PRODUTO_INDISPONIVEL",
                    "O produto " + produto.getNome() + " não está disponível para pedido."
            );
        }

        if (!Boolean.TRUE.equals(produto.getCategoria().getAtiva())) {
            throw new RegraNegocioException(
                    "CATEGORIA_INATIVA",
                    "O produto " + produto.getNome() + " pertence a uma categoria inativa."
            );
        }
    }

    private void validarItensDuplicados(List<ItemPedidoCriacaoRequest> itens) {
        Set<UUID> produtosIds = new HashSet<>();

        for (ItemPedidoCriacaoRequest item : itens) {
            if (!produtosIds.add(item.produtoId())) {
                throw new RegraNegocioException(
                        "PRODUTO_DUPLICADO_NO_PEDIDO",
                        "O mesmo produto não deve ser enviado mais de uma vez no pedido."
                );
            }
        }
    }

    private String gerarNumeroPedido() {
        Number proximoNumero = (Number) entityManager
                .createNativeQuery("SELECT nextval('pedidos_numero_seq')")
                .getSingleResult();

        return "P" + proximoNumero.longValue();
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itens = pedido.getItens()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getRestaurante().getId(),
                pedido.getRestaurante().getNome(),
                pedido.getNumeroPedido(),
                pedido.getClienteNome(),
                pedido.getTipoConsumo(),
                pedido.getStatusPedido(),
                pedido.getValorTotal(),
                itens,
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm()
        );
    }

    private ItemPedidoResponse toItemResponse(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getNomeProduto(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal(),
                item.getObservacao()
        );
    }
}
