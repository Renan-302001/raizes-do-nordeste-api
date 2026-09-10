package br.com.raizesdonordeste.api.domain.model;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "perfil")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_perfil")
    private UUID idPerfil;

    @Column(name = "codigo_perfil", nullable = false, unique = true, length = 50)
    private String codigoPerfil;

    @Column(name = "nome_perfil", nullable = false, unique = true, length = 100)
    private String nomePerfil;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_escopo", nullable = false, length = 20)
    private TipoEscopo tipoEscopo;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    protected Perfil() {
    }

    public Perfil(
            String codigoPerfil,
            String nomePerfil,
            String descricao,
            TipoEscopo tipoEscopo
    ){
        this.codigoPerfil = codigoPerfil;
        this.nomePerfil = nomePerfil;
        this.descricao = descricao;
        this.tipoEscopo = tipoEscopo;
        this.ativo = true;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public boolean possuiEscopoDeRede() {
        return this.tipoEscopo == TipoEscopo.REDE;
    }

    public boolean possuiEscopoDeUnidade() {
        return this.tipoEscopo == TipoEscopo.UNIDADE;
    }

    public UUID getIdPerfil() {
        return idPerfil;
    }

    public String getCodigoPerfil() {
        return codigoPerfil;
    }

    public String getNomePerfil() {
        return nomePerfil;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoEscopo getTipoEscopo() {
        return tipoEscopo;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
