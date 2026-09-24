package br.com.raizesdonordeste.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroProdutoRequest {

    @NotBlank(message = "O código público é obrigatório.")
    @Size(max = 30, message = "O código público deve ter no máximo 30 caracteres.")
    private String codigoPublico;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nome;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String descricao;

    @NotBlank(message = "A categoria é obrigatória.")
    @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres.")
    private String categoria;

    @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres.")
    private String imagemUrl;

    public String getCodigoPublico() {
        return codigoPublico;
    }

    public void setCodigoPublico(String codigoPublico) {
        this.codigoPublico = codigoPublico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}
