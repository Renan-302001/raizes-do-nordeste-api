package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.domain.model.StatusUnidade;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UnidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public UnidadeService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional(readOnly = true)
    public Page<Unidade> listarAtivas(Pageable pageable) {
        return unidadeRepository.findByStatusUnidade(StatusUnidade.ATIVA, pageable);
    }

    @Transactional(readOnly = true)
    public Unidade buscar(UUID idUnidade) {
        return unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Unidade não encontrada."
                ));
    }
}
