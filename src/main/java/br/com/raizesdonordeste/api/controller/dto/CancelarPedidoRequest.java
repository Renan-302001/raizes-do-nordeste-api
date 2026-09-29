package br.com.raizesdonordeste.api.controller.dto;

import jakarta.validation.constraints.Size;

public class CancelarPedidoRequest {

    @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres.")
    private String motivo;

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
