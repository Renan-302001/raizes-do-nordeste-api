package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
