package br.com.raizesdonordeste.api.application;


import br.com.raizesdonordeste.api.application.exception.CadastroDuplicadoException;
import br.com.raizesdonordeste.api.application.exception.SenhaInvalidaException;
import br.com.raizesdonordeste.api.domain.model.Cliente;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ClienteRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
public class CadastroClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroClienteService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Cliente cadastrar(
            String nome,
            String email,
            String senha,
            LocalDate dataNascimento,
            String cpf
    ) {
        if (senha == null || senha.isBlank() || senha.length() < 15) {
            throw new SenhaInvalidaException(
                    "A senha deve ter pelo menos 15 caracteres e não pode ser composta apenas por espaços."
            );
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new SenhaInvalidaException(
                    "A senha é muito longa. Reduza seu tamanho."
            );
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new CadastroDuplicadoException(
                    "O e-mail informado já está cadastrado."
            );
        }
        if (clienteRepository.existsByCpf(cpf)) {
            throw new CadastroDuplicadoException(
                    "O CPF informado já está cadastrado."
            );
        }

        String senhaHash = passwordEncoder.encode(senha);
        Cliente cliente = new Cliente(nome,
                                        email,
                                        senhaHash,
                                        dataNascimento,
                                        cpf
        );
        return clienteRepository.save(cliente);
    }
}
