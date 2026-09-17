package br.com.raizesdonordeste.api.application;


import br.com.raizesdonordeste.api.domain.model.Cliente;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ClienteRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CadastroClienteServiceTest {

    @Autowired
    private CadastroClienteService cadastroClienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveCadastrarClienteComSenhaProtegida() {

        String senha = "SenhaDeTesteSegura123!";
        String email = "cadastro.service" + UUID.randomUUID() + "@example.com";
        String nome = "NomeTeste";
        String cpf = "11122233344";
        LocalDate dataNascimento = LocalDate.of(1998,5,20);

        Cliente cliente = cadastroClienteService.cadastrar(
                nome,
                email,
                senha,
                dataNascimento,
                cpf
        );
        UUID idCliente = cliente.getIdUsuario();

        entityManager.flush();
        entityManager.clear();

        Cliente encontrado = clienteRepository.findById(idCliente).orElseThrow();

        assertNotEquals(senha, encontrado.getSenhaHash());
        assertTrue(passwordEncoder.matches(senha, encontrado.getSenhaHash()));
        assertFalse(passwordEncoder.matches(
                "OutraSenhaDeTeste123!",
                encontrado.getSenhaHash()
        ));

    }
}
