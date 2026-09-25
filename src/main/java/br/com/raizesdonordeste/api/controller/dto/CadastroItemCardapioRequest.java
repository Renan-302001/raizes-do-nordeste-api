package br.com.raizesdonordeste.api.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CadastroItemCardapioRequest {

    @NotNull(message = "O produto é obrigatório.")
    private UUID idProduto;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal preco;

    private Instant inicioVigencia;
    private Instant fimVigencia;

    public UUID getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(UUID idProduto) {
        this.idProduto = idProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Instant getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Instant inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Instant getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Instant fimVigencia) {
        this.fimVigencia = fimVigencia;
    }
}
