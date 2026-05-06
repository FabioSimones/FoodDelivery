package bt.com.totem.backend.shared.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        String codigo,
        String mensagem,
        Integer status,
        String path,
        LocalDateTime timestamp,
        List<ErroCampo> erros
) {
}
