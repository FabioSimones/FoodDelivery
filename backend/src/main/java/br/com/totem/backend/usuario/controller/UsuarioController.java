package br.com.totem.backend.usuario.controller;

import br.com.totem.backend.usuario.dto.UsuarioCriacaoRequest;
import br.com.totem.backend.usuario.dto.UsuarioResponse;
import br.com.totem.backend.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@RequestBody @Valid UsuarioCriacaoRequest request) {
        return usuarioService.criar(request);
    }
}
