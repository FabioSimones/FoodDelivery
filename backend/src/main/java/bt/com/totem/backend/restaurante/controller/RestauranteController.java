package bt.com.totem.backend.restaurante.controller;

import bt.com.totem.backend.restaurante.dto.RestauranteCriacaoRequest;
import bt.com.totem.backend.restaurante.dto.RestauranteResponse;
import bt.com.totem.backend.restaurante.service.RestauranteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {
    private final RestauranteService restauranteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteResponse criar(@RequestBody @Valid RestauranteCriacaoRequest request) {
        return restauranteService.criar(request);
    }

    @GetMapping
    public List<RestauranteResponse> listar() {
        return restauranteService.listar();
    }
}
