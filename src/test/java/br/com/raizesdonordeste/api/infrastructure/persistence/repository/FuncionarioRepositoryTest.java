package br.com.raizesdonordeste.api.infrastructure.persistence.repository;


import br.com.raizesdonordeste.api.domain.model.Funcionario;
import br.com.raizesdonordeste.api.domain.model.Perfil;
import br.com.raizesdonordeste.api.domain.model.TipoEscopo;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class FuncionarioRepositoryTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarFuncionarioComUnidadeEPerfil() {

        String email = "emailTeste@example.com";
        String matricula = "1234567";

        Unidade unidade = new Unidade("unidadeTeste", "12345678901234", "1234567890123", "centro", "123", "ComplementoTeste", "centro", "São Paulo", "SP", "12345678");
        unidadeRepository.saveAndFlush(unidade);
        UUID idUnidade = unidade.getIdUnidade();

        Perfil perfil = new Perfil("codigo" + UUID.randomUUID(), "nome" + UUID.randomUUID(), "Perfil de teste de funcionário", TipoEscopo.UNIDADE);
        perfilRepository.saveAndFlush(perfil);
        UUID idPerfil = perfil.getIdPerfil();

        Funcionario funcionario = new Funcionario("nome", email, "hashTeste_123", LocalDate.of(2001, 6, 30), unidade, perfil, matricula, LocalDate.of(2026,1,10));
        funcionarioRepository.saveAndFlush(funcionario);
        UUID idFuncionario = funcionario.getIdUsuario();

        entityManager.clear();

        Funcionario encontrado = funcionarioRepository.findByMatricula(matricula).orElseThrow();

        assertNotNull(idFuncionario);
        assertEquals(idFuncionario, encontrado.getIdUsuario());
        assertEquals(idUnidade, encontrado.getUnidade().getIdUnidade());
        assertEquals(idPerfil, encontrado.getPerfil().getIdPerfil());



    }
}
