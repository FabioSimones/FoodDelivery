package br.com.totem.backend.auth.controller;

import br.com.totem.backend.auth.dto.LoginRequest;
import br.com.totem.backend.auth.dto.LoginResponse;
import br.com.totem.backend.auth.service.AuthService;
import br.com.totem.backend.usuario.dto.UsuarioCriacaoRequest;
import br.com.totem.backend.usuario.dto.UsuarioResponse;
import br.com.totem.backend.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/bootstrap/super-admin")
    public UsuarioResponse criarSuperAdminInicial(
            @RequestBody @Valid UsuarioCriacaoRequest request
    ) {
        return usuarioService.criarSuperAdminBootstrap(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
