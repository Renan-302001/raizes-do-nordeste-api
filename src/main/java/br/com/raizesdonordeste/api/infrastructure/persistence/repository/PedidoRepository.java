package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Page<Pedido> findByCanalPedido(CanalPedido canalPedido, Pageable pageable);

    Page<Pedido> findByCanalPedidoAndStatusPedido(
            CanalPedido canalPedido,
            StatusPedido statusPedido,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"itens", "itens.produto", "unidade", "cliente"})
    Optional<Pedido> findDetalhadoByIdPedido(UUID idPedido);
}
