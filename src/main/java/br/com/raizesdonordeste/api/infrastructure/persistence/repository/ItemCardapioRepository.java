package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusItemCardapio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, UUID> {
    Optional<ItemCardapio> findByCardapio_Unidade_IdUnidadeAndCardapio_StatusCardapioAndProduto_IdProdutoAndStatusItem(
            UUID idUnidade,
            StatusCardapio statusCardapio,
            UUID idProduto,
            StatusItemCardapio statusItem
    );
}
