package bt.com.totem.backend.categoria.entity;

import bt.com.totem.backend.restaurante.entity.Restaurante;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(name = "ordem_exibicao", nullable = false)
    private Integer ordemExibicao;

    @Column(nullable = false)
    private Boolean ativa;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();

        if (this.ativa == null) {
            this.ativa = true;
        }

        if (this.ordemExibicao == null) {
            this.ordemExibicao = 0;
        }

        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
