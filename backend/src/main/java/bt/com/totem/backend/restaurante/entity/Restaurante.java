package bt.com.totem.backend.restaurante.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 20)
    private String cnpj;

    @Column(length = 255)
    private String endereco;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
