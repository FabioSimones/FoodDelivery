package bt.com.totem.backend.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> tratarConflito(
            ConflitoException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroResponse response = new ErroResponse(
                ex.getCodigo(),
                ex.getMessage(),
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroResponse response = new ErroResponse(
                ex.getCodigo(),
                ex.getMessage(),
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(
            RegraNegocioException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErroResponse response = new ErroResponse(
                ex.getCodigo(),
                ex.getMessage(),
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ErroCampo> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toErroCampo)
                .toList();

        ErroResponse response = new ErroResponse(
                "VALIDACAO_ERRO",
                "Existem campos inválidos na requisição.",
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                erros
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> tratarConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ErroCampo> erros = ex.getConstraintViolations()
                .stream()
                .map(violacao -> new ErroCampo(
                        violacao.getPropertyPath().toString(),
                        violacao.getMessage()
                ))
                .toList();

        ErroResponse response = new ErroResponse(
                "VALIDACAO_ERRO",
                "Existem parâmetros inválidos na requisição.",
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                erros
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarErroIntegridadeBanco(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroResponse response = new ErroResponse(
                "ERRO_INTEGRIDADE_DADOS",
                "Não foi possível concluir a operação porque existe conflito com os dados já cadastrados.",
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> tratarIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErroResponse response = new ErroResponse(
                "DADOS_INVALIDOS",
                ex.getMessage(),
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInterno(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErroResponse response = new ErroResponse(
                "ERRO_INTERNO",
                "Ocorreu um erro interno inesperado.",
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroObrigatorio(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErroResponse response = new ErroResponse(
                "PARAMETRO_OBRIGATORIO",
                "Um parâmetro obrigatório não foi informado.",
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of(new ErroCampo(
                        ex.getParameterName(),
                        "O parâmetro " + ex.getParameterName() + " é obrigatório."
                ))
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarParametroInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErroResponse response = new ErroResponse(
                "PARAMETRO_INVALIDO",
                "Um parâmetro informado possui formato inválido.",
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of(new ErroCampo(
                        ex.getName(),
                        "O parâmetro " + ex.getName() + " possui valor inválido."
                ))
        );

        return ResponseEntity.status(status).body(response);
    }

    private ErroCampo toErroCampo(FieldError fieldError) {
        return new ErroCampo(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }
}
