package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Pagamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    @EntityGraph(attributePaths = "pedido")
    Optional<Pagamento> findByChaveIdempotencia(String chaveIdempotencia);

    @EntityGraph(attributePaths = "pedido")
    Optional<Pagamento> findDetalhadoByIdPagamento(UUID idPagamento);

    List<Pagamento> findByPedido_IdPedidoOrderBySolicitadoEmDesc(UUID idPedido);
}
