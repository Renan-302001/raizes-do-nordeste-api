package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.DiaSemana;
import br.com.raizesdonordeste.api.domain.model.HorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, UUID> {
    List<HorarioFuncionamento> findByUnidade_IdUnidade(UUID idUnidade);

    Optional<HorarioFuncionamento> findByUnidade_IdUnidadeAndDiaSemana(UUID idUnidade, DiaSemana diaSemana);
}
