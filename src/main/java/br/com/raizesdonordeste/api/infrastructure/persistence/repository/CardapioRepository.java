package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Cardapio;
import br.com.raizesdonordeste.api.domain.model.StatusCardapio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardapioRepository extends JpaRepository<Cardapio, UUID> {
    @EntityGraph(attributePaths = "unidade")
    Optional<Cardapio> findByUnidade_IdUnidadeAndStatusCardapio(
            UUID idUnidade,
            StatusCardapio statusCardapio
    );
}
