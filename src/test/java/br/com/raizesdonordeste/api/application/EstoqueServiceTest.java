package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.domain.model.TipoMovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.domain.model.Usuario;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.EstoqueProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.MovimentacaoEstoqueRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UnidadeRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueProdutoRepository estoqueProdutoRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Mock
    private UnidadeRepository unidadeRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Usuario usuario;

    @InjectMocks
    private EstoqueService estoqueService;

    @Test
    void deveCriarEstoqueERegistrarEntrada() {
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        Unidade unidade = novaUnidade();
        Produto produto = novoProduto();

        prepararReferencias(idUnidade, idProduto, idUsuario, unidade, produto);
        when(estoqueProdutoRepository.buscarParaAtualizacao(idUnidade, idProduto))
                .thenReturn(Optional.empty());
        when(estoqueProdutoRepository.saveAndFlush(any(EstoqueProduto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MovimentacaoEstoque movimentacao = estoqueService.movimentar(
                idUnidade,
                idProduto,
                idUsuario,
                TipoMovimentacaoEstoque.ENTRADA,
                10,
                "Carga inicial"
        );

        assertEquals(0, movimentacao.getSaldoDisponivelAnterior());
        assertEquals(10, movimentacao.getSaldoDisponivelPosterior());
        assertEquals(10, movimentacao.getEstoqueProduto().getQuantidadeDisponivel());
    }

    @Test
    void deveRejeitarSaidaComSaldoInsuficiente() {
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        Unidade unidade = novaUnidade();
        Produto produto = novoProduto();
        EstoqueProduto estoque = new EstoqueProduto(unidade, produto, 3, 0);

        prepararReferencias(idUnidade, idProduto, idUsuario, unidade, produto);
        when(estoqueProdutoRepository.buscarParaAtualizacao(idUnidade, idProduto))
                .thenReturn(Optional.of(estoque));

        assertThrows(
                RegraNegocioException.class,
                () -> estoqueService.movimentar(
                        idUnidade,
                        idProduto,
                        idUsuario,
                        TipoMovimentacaoEstoque.SAIDA,
                        5,
                        "Saída inválida"
                )
        );

        verify(movimentacaoRepository, never())
                .save(any(MovimentacaoEstoque.class));
    }

    private void prepararReferencias(
            UUID idUnidade,
            UUID idProduto,
            UUID idUsuario,
            Unidade unidade,
            Produto produto
    ) {
        when(unidadeRepository.findById(idUnidade)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
    }

    private Unidade novaUnidade() {
        return new Unidade(
                "Unidade Teste",
                "12345678000199",
                "UNI-TESTE",
                "Rua Teste",
                "10",
                null,
                "Centro",
                "Recife",
                "PE",
                "50000000"
        );
    }

    private Produto novoProduto() {
        return new Produto(
                "PROD-TESTE",
                "Cuscuz",
                null,
                "CAFE_DA_MANHA",
                null
        );
    }
}
