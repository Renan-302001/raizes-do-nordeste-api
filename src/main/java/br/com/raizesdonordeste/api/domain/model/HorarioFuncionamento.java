package br.com.raizesdonordeste.api.domain.model;


import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "horario_funcionamento",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_horario_unidade_dia",
                columnNames = {"id_unidade", "dia_semana"}
        )
)
public class HorarioFuncionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_horario_funcionamento")
    private UUID idHorarioFuncionamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DiaSemana diaSemana;

    @Column(name = "hora_abertura")
    private LocalTime horaAbertura;

    @Column(name = "hora_fechamento")
    private LocalTime horaFechamento;

    @Column(name = "fechado", nullable = false)
    private boolean fechado;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected HorarioFuncionamento() {
    }

    public HorarioFuncionamento(
            Unidade unidade,
            DiaSemana diaSemana,
            LocalTime horaAbertura,
            LocalTime horaFechamento,
            boolean fechado
    ) {
        if (unidade == null || diaSemana == null){
            throw new IllegalArgumentException(
                    "Unidade e dia da semana são obrigatórios."
            );
        }
        if (fechado && (horaAbertura != null || horaFechamento != null)){
            throw new IllegalArgumentException(
                    "Um dia fechado não deve possuir horários de abertura e fechamento."
            );
        }
        if (!fechado && (horaAbertura == null || horaFechamento == null)) {
            throw new IllegalArgumentException(
                    "Um dia aberto deve possuir horários de abertura e fechamento."
            );
        }
        this.unidade = unidade;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.fechado = fechado;
    }

    @PrePersist
    @PreUpdate
    protected void atualizarDataControle() {
        this.atualizadoEm = Instant.now();
    }

    public UUID getIdHorarioFuncionamento() {
        return idHorarioFuncionamento;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraAbertura() {
        return horaAbertura;
    }

    public LocalTime getHoraFechamento() {
        return horaFechamento;
    }

    public boolean getFechado() {
        return fechado;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
