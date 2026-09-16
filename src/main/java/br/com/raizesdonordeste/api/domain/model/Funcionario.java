package br.com.raizesdonordeste.api.domain.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "funcionario")
@PrimaryKeyJoinColumn(
        name = "id_funcionario",
        referencedColumnName = "id_usuario"
)
public class Funcionario extends Usuario {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @Column(name = "matricula", nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(name = "data_desligamento")
    private LocalDate dataDesligamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_vinculo", nullable = false, length = 20)
    private StatusVinculo statusVinculo = StatusVinculo.ATIVO;

    protected Funcionario() {
    }

    public Funcionario(
            String nome,
            String email,
            String senhaHash,
            LocalDate dataNascimento,
            Unidade unidade,
            Perfil perfil,
            String matricula,
            LocalDate dataAdmissao
    ) {
        super(nome, email, senhaHash, dataNascimento);
        this.unidade = unidade;
        this.perfil = perfil;
        this.matricula = matricula;
        this.dataAdmissao = dataAdmissao;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public String getMatricula() {
        return matricula;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public LocalDate getDataDesligamento() {
        return dataDesligamento;
    }

    public StatusVinculo getStatusVinculo() {
        return statusVinculo;
    }
}
