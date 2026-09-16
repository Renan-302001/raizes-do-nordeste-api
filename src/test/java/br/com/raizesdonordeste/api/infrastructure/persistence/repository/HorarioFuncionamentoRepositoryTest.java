package br.com.raizesdonordeste.api.infrastructure.persistence.repository;


import br.com.raizesdonordeste.api.domain.model.DiaSemana;
import br.com.raizesdonordeste.api.domain.model.HorarioFuncionamento;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class HorarioFuncionamentoRepositoryTest {

    @Autowired
    private HorarioFuncionamentoRepository horarioFuncionamentoRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarEBuscarHorarioPorUnidadeEDia() {

        Unidade unidade = new Unidade("unidadeTeste",
                                    "12345678901234",
                                    "1234567890123",
                                    "centro",
                                    "123",
                                    "ComplementoTeste",
                                    "centro",
                                    "São Paulo",
                                    "SP",
                                    "12345678");
        unidadeRepository.saveAndFlush(unidade);
        UUID idUnidade = unidade.getIdUnidade();

        HorarioFuncionamento horario = new HorarioFuncionamento(
                unidade,
                DiaSemana.SEGUNDA,
                LocalTime.of(8,0),
                LocalTime.of(18,0),
                false
        );
        horarioFuncionamentoRepository.saveAndFlush(horario);
        UUID idHorario = horario.getIdHorarioFuncionamento();

        entityManager.clear();

        HorarioFuncionamento encontrado = horarioFuncionamentoRepository.findByUnidade_IdUnidadeAndDiaSemana(idUnidade, DiaSemana.SEGUNDA).orElseThrow();

        assertNotNull(idHorario);
        assertEquals(idUnidade, encontrado.getUnidade().getIdUnidade());
        assertEquals(idHorario, encontrado.getIdHorarioFuncionamento());
        assertEquals(DiaSemana.SEGUNDA, encontrado.getDiaSemana());
        assertEquals(LocalTime.of(8,0), encontrado.getHoraAbertura());
        assertEquals(LocalTime.of(18,0), encontrado.getHoraFechamento());
        assertFalse(encontrado.getFechado());

    }
}
