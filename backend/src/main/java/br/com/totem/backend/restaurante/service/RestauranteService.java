package br.com.totem.backend.restaurante.service;

import br.com.totem.backend.restaurante.dto.RestauranteCriacaoRequest;
import br.com.totem.backend.restaurante.dto.RestauranteResponse;
import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.restaurante.repository.RestauranteRepository;
import br.com.totem.backend.shared.exception.ConflitoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteResponse criar(RestauranteCriacaoRequest request) {
        String cnpjNormalizado = normalizarCnpj(request.cnpj());

        if (cnpjNormalizado != null && restauranteRepository.existsByCnpj(cnpjNormalizado)) {
            throw new ConflitoException(
                    "CNPJ_DUPLICADO",
                    "Já existe um restaurante cadastrado com este CNPJ."
            );
        }

        Restaurante restaurante = Restaurante.builder()
                .nome(request.nome())
                .cnpj(cnpjNormalizado)
                .endereco(request.endereco())
                .build();

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);

        return toResponse(restauranteSalvo);
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return null;
        }

        String somenteNumeros = cnpj.replaceAll("\\D", "");

        if (somenteNumeros.length() != 14) {
            throw new RegraNegocioException(
                    "CNPJ_INVALIDO",
                    "CNPJ deve possuir 14 dígitos."
            );
        }

        return somenteNumeros;
    }

    public List<RestauranteResponse> listar() {
        return restauranteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RestauranteResponse toResponse(Restaurante restaurante) {
        return new RestauranteResponse(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getCnpj(),
                restaurante.getEndereco(),
                restaurante.getAtivo(),
                restaurante.getCriadoEm(),
                restaurante.getAtualizadoEm()
        );
    }
}
