package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.HistoricoStatusPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoStatusPedidoRepository
        extends JpaRepository<HistoricoStatusPedido, UUID> {

    @EntityGraph(attributePaths = {"pedido", "usuarioResponsavel"})
    List<HistoricoStatusPedido> findByPedido_IdPedidoOrderByAlteradoEmAsc(
            UUID idPedido
    );
}
