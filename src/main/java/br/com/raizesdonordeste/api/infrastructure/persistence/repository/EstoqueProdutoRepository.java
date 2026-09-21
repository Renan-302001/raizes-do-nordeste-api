package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EstoqueProdutoRepository extends JpaRepository<EstoqueProduto, UUID> {

    Optional<EstoqueProduto> findByUnidade_IdUnidadeAndProduto_IdProduto(
            UUID idUnidade,
            UUID idProduto
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT estoque
            FROM EstoqueProduto estoque
            WHERE estoque.unidade.idUnidade = :idUnidade
              AND estoque.produto.idProduto = :idProduto
            """)
    Optional<EstoqueProduto> buscarParaAtualizacao(
            @Param("idUnidade") UUID idUnidade,
            @Param("idProduto") UUID idProduto
    );
}
