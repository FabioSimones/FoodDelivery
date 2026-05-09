package br.com.totem.backend.shared.exception;

public class RegraNegocioException extends RuntimeException {
    private final String codigo;

    public RegraNegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
