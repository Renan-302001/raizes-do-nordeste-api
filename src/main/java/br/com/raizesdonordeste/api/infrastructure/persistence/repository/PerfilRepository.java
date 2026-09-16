package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<Perfil, UUID> {
    Optional<Perfil> findByCodigoPerfil(String codigoPerfil);
}
