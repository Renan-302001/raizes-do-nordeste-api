package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovimentacaoEstoqueRepository
        extends JpaRepository<MovimentacaoEstoque, UUID> {

    Page<MovimentacaoEstoque> findByEstoqueProduto_IdEstoqueProduto(
            UUID idEstoqueProduto,
            Pageable pageable
    );
}
