package br.com.raizesdonordeste.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_produto")
    private UUID idProduto;

    @Column(name = "codigo_publico", nullable = false, unique = true, length = 30)
    private String codigoPublico;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_produto", nullable = false, length = 20)
    private StatusProduto statusProduto = StatusProduto.ATIVO;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "encerrado_em")
    private Instant encerradoEm;

    protected Produto() {
    }

    public Produto(
            String codigoPublico,
            String nome,
            String descricao,
            String categoria,
            String imagemUrl
    ) {
        this.codigoPublico = codigoPublico;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
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
        this.statusProduto = StatusProduto.ATIVO;
        this.encerradoEm = null;
    }

    public void inativar() {
        this.statusProduto = StatusProduto.INATIVO;
    }

    public void encerrar() {
        this.statusProduto = StatusProduto.ENCERRADO;
        this.encerradoEm = Instant.now();
    }

    public UUID getIdProduto() {
        return idProduto;
    }

    public String getCodigoPublico() {
        return codigoPublico;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public StatusProduto getStatusProduto() {
        return statusProduto;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public Instant getEncerradoEm() {
        return encerradoEm;
    }
}
