package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.CadastroDuplicadoException;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveCadastrarProdutoComCodigoNormalizado() {
        when(produtoRepository.existsByCodigoPublico("CUSCUZ-001"))
                .thenReturn(false);
        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto produto = produtoService.cadastrar(
                "  cuscuz-001  ",
                "  Cuscuz Tradicional  ",
                "Cuscuz nordestino",
                "CAFE_DA_MANHA",
                null
        );

        assertEquals("CUSCUZ-001", produto.getCodigoPublico());
        assertEquals("Cuscuz Tradicional", produto.getNome());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveRejeitarCodigoPublicoDuplicado() {
        when(produtoRepository.existsByCodigoPublico("CUSCUZ-001"))
                .thenReturn(true);

        assertThrows(
                CadastroDuplicadoException.class,
                () -> produtoService.cadastrar(
                        "CUSCUZ-001",
                        "Cuscuz Tradicional",
                        null,
                        "CAFE_DA_MANHA",
                        null
                )
        );

        verify(produtoRepository, never()).save(any(Produto.class));
    }
}
