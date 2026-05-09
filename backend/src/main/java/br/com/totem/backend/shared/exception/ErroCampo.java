package br.com.totem.backend.shared.exception;

public record ErroCampo(
        String campo,
        String mensagem
) {
}
