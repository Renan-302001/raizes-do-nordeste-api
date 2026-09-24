package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.domain.model.StatusUnidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {
    Optional<Unidade> findByCodigoPublico(String codigoPublico);
    boolean existsByCnpj(String cnpj);
    Page<Unidade> findByStatusUnidade(StatusUnidade statusUnidade, Pageable pageable);
}
