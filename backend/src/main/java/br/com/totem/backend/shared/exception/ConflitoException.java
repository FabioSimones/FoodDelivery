package br.com.totem.backend.shared.exception;

public class ConflitoException extends RuntimeException {
    private final String codigo;

    public ConflitoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
