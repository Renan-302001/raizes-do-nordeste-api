package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    Optional<Pagamento> findByChaveIdempotencia(String chaveIdempotencia);
    List<Pagamento> findByPedido_IdPedidoOrderBySolicitadoEmDesc(UUID idPedido);
}
