package br.com.raizesdonordeste.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cardapio")
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cardapio")
    private UUID idCardapio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cardapio", nullable = false, length = 20)
    private StatusCardapio statusCardapio = StatusCardapio.INATIVO;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Cardapio() {
    }

    public Cardapio(Unidade unidade, String nome) {
        this.unidade = unidade;
        this.nome = nome;
    }

    @PrePersist
    protected void aoCriar() {
        Instant agora = Instant.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    public void ativar() {
        this.statusCardapio = StatusCardapio.ATIVO;
    }

    public void inativar() {
        this.statusCardapio = StatusCardapio.INATIVO;
    }

    public UUID getIdCardapio() {
        return idCardapio;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public String getNome() {
        return nome;
    }

    public StatusCardapio getStatusCardapio() {
        return statusCardapio;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
