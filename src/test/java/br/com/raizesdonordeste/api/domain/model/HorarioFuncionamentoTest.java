package br.com.raizesdonordeste.api.domain.model;


import org.junit.jupiter.api.Test;
import java.time.LocalTime;


import static org.junit.jupiter.api.Assertions.assertThrows;
class HorarioFuncionamentoTest {


    @Test
    void deveRejeitarDiaFechadoComHorarios() {
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


        assertThrows(IllegalArgumentException.class, () -> {
            new HorarioFuncionamento(
                    unidade,
                    DiaSemana.SEGUNDA,
                    LocalTime.of(8, 0),
                    LocalTime.of(18, 0),
                    true
            );
        });
    }
    @Test
    void deveRejeitarDiaAbertoSemHorarios() {
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

        assertThrows(IllegalArgumentException.class, () -> {
            new HorarioFuncionamento(
                    unidade,
                    DiaSemana.SEGUNDA,
                    null,
                    null,
                    false
            );
        });
    }
}