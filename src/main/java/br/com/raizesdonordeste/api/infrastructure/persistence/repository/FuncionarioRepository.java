package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Funcionario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {
    Optional<Funcionario> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);
    Page<Funcionario> findByUnidade_IdUnidade(UUID idUnidade, Pageable pageable);
}
