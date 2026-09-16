package br.com.raizesdonordeste.api.infrastructure.persistence.repository;

import br.com.raizesdonordeste.api.domain.model.Perfil;
import br.com.raizesdonordeste.api.domain.model.TipoEscopo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class PerfilRepositoryTest {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveRetornarVazioQuandoCodigoNaoExiste() {
        String codigo = "INEXISTENTE" + UUID.randomUUID();

        Optional<Perfil> resultado = perfilRepository.findByCodigoPerfil(codigo);

        assertTrue(resultado.isEmpty());
    }
    @Test
    void deveSalvarEBuscarPerfilPorCodigo() {
        // Preparar dados exclusivos para esta execução.
        String sufixo = UUID.randomUUID().toString();
        String codigo = "TESTE_" + sufixo;
        String nome = "Perfil de teste " + sufixo;
        Perfil perfil = new Perfil(codigo, nome, "Teste de persistência", TipoEscopo.UNIDADE);

        // Enviar o INSERT ao banco e remover os objetos do contexto do JPA.
        perfilRepository.saveAndFlush(perfil);
        UUID id = perfil.getIdPerfil();
        entityManager.clear();

        // Consultar novamente e verificar os valores recuperados.
        Perfil encontrado = perfilRepository.findByCodigoPerfil(codigo).orElseThrow();

        assertNotNull(id);
        assertEquals(id, encontrado.getIdPerfil());
        assertEquals(codigo, encontrado.getCodigoPerfil());
        assertEquals(nome, encontrado.getNomePerfil());
        assertEquals("Teste de persistência", encontrado.getDescricao());
        assertEquals(TipoEscopo.UNIDADE, encontrado.getTipoEscopo());
        assertTrue(encontrado.isAtivo());
    }
}
