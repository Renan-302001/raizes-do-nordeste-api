package br.com.raizesdonordeste.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve possuir formato válido.")
    @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
    @Schema(example = "cliente@example.com")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Schema(example = "SenhaSegura123!", format = "password")
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
