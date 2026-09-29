package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Override
    @EntityGraph(attributePaths = "unidade")
    Page<Pedido> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "unidade")
    Page<Pedido> findByCanalPedido(CanalPedido canalPedido, Pageable pageable);

    @EntityGraph(attributePaths = "unidade")
    Page<Pedido> findByStatusPedido(StatusPedido statusPedido, Pageable pageable);

    @EntityGraph(attributePaths = "unidade")
    Page<Pedido> findByCanalPedidoAndStatusPedido(
            CanalPedido canalPedido,
            StatusPedido statusPedido,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "itens",
            "itens.produto",
            "unidade",
            "cliente",
            "usuarioCriador"
    })
    Optional<Pedido> findDetalhadoByIdPedido(UUID idPedido);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pedido FROM Pedido pedido WHERE pedido.idPedido = :idPedido")
    Optional<Pedido> buscarParaProcessamentoPagamento(
            @Param("idPedido") UUID idPedido
    );
}
