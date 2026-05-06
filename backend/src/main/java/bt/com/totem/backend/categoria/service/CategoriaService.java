package bt.com.totem.backend.categoria.service;

import bt.com.totem.backend.categoria.dto.CategoriaAtualizacaoRequest;
import bt.com.totem.backend.categoria.dto.CategoriaCriacaoRequest;
import bt.com.totem.backend.categoria.dto.CategoriaResponse;
import bt.com.totem.backend.categoria.entity.Categoria;
import bt.com.totem.backend.categoria.repository.CategoriaRepository;
import bt.com.totem.backend.restaurante.entity.Restaurante;
import bt.com.totem.backend.restaurante.repository.RestauranteRepository;
import bt.com.totem.backend.shared.exception.ConflitoException;
import bt.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import bt.com.totem.backend.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final RestauranteRepository restauranteRepository;

    public CategoriaResponse criar(CategoriaCriacaoRequest request) {
        Restaurante restaurante = buscarRestaurante(request.restauranteId());

        String nomeNormalizado = normalizarNome(request.nome());

        if (categoriaRepository.existsByRestaurante_IdAndNomeIgnoreCase(
                request.restauranteId(),
                nomeNormalizado
        )) {
            throw new ConflitoException(
                    "CATEGORIA_DUPLICADA",
                    "Já existe uma categoria cadastrada com este nome para este restaurante."
            );
        }

        Categoria categoria = Categoria.builder()
                .restaurante(restaurante)
                .nome(nomeNormalizado)
                .descricao(normalizarTextoOpcional(request.descricao()))
                .ordemExibicao(request.ordemExibicao())
                .ativa(true)
                .build();

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return toResponse(categoriaSalva);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(UUID restauranteId) {
        if (restauranteId == null) {
            return categoriaRepository.listarTodasOrdenadas()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        buscarRestaurante(restauranteId);

        return categoriaRepository.listarPorRestauranteOrdenadas(restauranteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(UUID id) {
        Categoria categoria = buscarCategoria(id);
        return toResponse(categoria);
    }

    public CategoriaResponse atualizar(UUID id, CategoriaAtualizacaoRequest request) {
        Categoria categoria = buscarCategoria(id);

        UUID restauranteId = categoria.getRestaurante().getId();
        String nomeNormalizado = normalizarNome(request.nome());

        if (categoriaRepository.existsByRestaurante_IdAndNomeIgnoreCaseAndIdNot(
                restauranteId,
                nomeNormalizado,
                id
        )) {
            throw new ConflitoException(
                    "CATEGORIA_DUPLICADA",
                    "Já existe outra categoria cadastrada com este nome para este restaurante."
            );
        }

        categoria.setNome(nomeNormalizado);
        categoria.setDescricao(normalizarTextoOpcional(request.descricao()));
        categoria.setOrdemExibicao(request.ordemExibicao());

        if (request.ativa() != null) {
            categoria.setAtiva(request.ativa());
        }

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return toResponse(categoriaAtualizada);
    }

    public void desativar(UUID id) {
        Categoria categoria = buscarCategoria(id);

        if (Boolean.FALSE.equals(categoria.getAtiva())) {
            throw new RegraNegocioException(
                    "CATEGORIA_JA_INATIVA",
                    "Esta categoria já está inativa."
            );
        }

        categoria.setAtiva(false);
        categoriaRepository.save(categoria);
    }

    private Restaurante buscarRestaurante(UUID restauranteId) {
        return restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "RESTAURANTE_NAO_ENCONTRADO",
                        "Restaurante não encontrado."
                ));
    }

    private Categoria buscarCategoria(UUID id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "CATEGORIA_NAO_ENCONTRADA",
                        "Categoria não encontrada."
                ));
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException(
                    "NOME_CATEGORIA_INVALIDO",
                    "O nome da categoria é obrigatório."
            );
        }

        return nome.trim();
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getRestaurante().getId(),
                categoria.getRestaurante().getNome(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getOrdemExibicao(),
                categoria.getAtiva(),
                categoria.getCriadoEm(),
                categoria.getAtualizadoEm()
        );
    }
}