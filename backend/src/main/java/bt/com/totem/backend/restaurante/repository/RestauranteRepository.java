package bt.com.totem.backend.restaurante.repository;

import bt.com.totem.backend.restaurante.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestauranteRepository extends JpaRepository<Restaurante, UUID> {

    boolean existsByCnpj(String cnpj);

    Optional<Restaurante> findByCnpj(String cnpj);
}
