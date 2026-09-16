package br.com.raizesdonordeste.api.infrastructure.persistence.repository;


import br.com.raizesdonordeste.api.domain.model.Cliente;
import br.com.raizesdonordeste.api.domain.model.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarClienteEBuscarComoUsuario() {
        String sufixo = UUID.randomUUID().toString();
        String email = "cliente." + sufixo + "@example.com";
        String cpf = "12345678901";
        LocalDate dataNascimento = LocalDate.of(2001, 6, 30);
        Cliente cliente = new Cliente("Cliente teste", email, "hash_ficticio_123", dataNascimento, cpf);

        clienteRepository.saveAndFlush(cliente);
        UUID id = cliente.getIdUsuario();
        entityManager.clear();

        Cliente clienteEncontrado = clienteRepository.findByCpf(cpf).orElseThrow();
        Usuario usuarioEncontrado = usuarioRepository.findByEmail(email).orElseThrow();

        assertNotNull(id);
        assertEquals(id, usuarioEncontrado.getIdUsuario());
        assertEquals(id, clienteEncontrado.getIdUsuario());
        assertEquals(cpf, clienteEncontrado.getCpf());
        assertEquals(email, usuarioEncontrado.getEmail());
        assertInstanceOf(Cliente.class, usuarioEncontrado);
    }
}
