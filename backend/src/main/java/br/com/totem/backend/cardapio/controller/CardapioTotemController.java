package br.com.totem.backend.cardapio.controller;

import br.com.totem.backend.cardapio.dto.CardapioTotemResponse;
import br.com.totem.backend.cardapio.service.CardapioTotemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/totem/cardapio")
@RequiredArgsConstructor
public class CardapioTotemController {

    private final CardapioTotemService cardapioTotemService;

    @GetMapping
    public CardapioTotemResponse buscarCardapio(
            @RequestParam UUID restauranteId
    ) {
        return cardapioTotemService.buscarCardapio(restauranteId);
    }
}
