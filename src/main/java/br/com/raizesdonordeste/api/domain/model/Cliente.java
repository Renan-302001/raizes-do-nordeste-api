package br.com.raizesdonordeste.api.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(
        name = "id_cliente",
        referencedColumnName = "id_usuario"
)
public class Cliente extends Usuario {
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    protected Cliente() {
    }

    public Cliente(
            String nome,
            String email,
            String senhaHash,
            LocalDate dataNascimento,
            String cpf
    ) {
        super(nome, email, senhaHash, dataNascimento);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
}
