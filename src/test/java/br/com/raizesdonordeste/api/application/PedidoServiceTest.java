package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.controller.dto.ItemPedidoRequest;
import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.domain.model.StatusCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusItemCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import br.com.raizesdonordeste.api.domain.model.StatusProduto;
import br.com.raizesdonordeste.api.domain.model.StatusUnidade;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.EstoqueProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ItemCardapioRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.MovimentacaoEstoqueRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PedidoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UnidadeRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UnidadeRepository unidadeRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ItemCardapioRepository itemCardapioRepository;
    @Mock
    private EstoqueProdutoRepository estoqueProdutoRepository;
    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    @Mock
    private Unidade unidade;
    @Mock
    private Produto produto;
    @Mock
    private ItemCardapio itemCardapio;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void deveCriarPedidoEReservarEstoque() {
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        EstoqueProduto estoque = prepararFluxoValido(idUnidade, idProduto, 10);
        ItemPedidoRequest item = novoItem(idProduto, 2);

        when(pedidoRepository.saveAndFlush(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(movimentacaoEstoqueRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido pedido = pedidoService.criar(
                idUnidade,
                CanalPedido.TOTEM,
                List.of(item),
                null
        );

        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatusPedido());
        assertEquals(new BigDecimal("37.80"), pedido.getValorTotal());
        assertEquals(1, pedido.getItens().size());
        assertEquals(2, estoque.getQuantidadeReservada());
        assertEquals(8, estoque.getQuantidadeParaVenda());
        verify(movimentacaoEstoqueRepository).saveAll(anyList());
    }

    @Test
    void deveRejeitarPedidoComEstoqueInsuficiente() {
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        EstoqueProduto estoque = prepararFluxoValido(idUnidade, idProduto, 3);
        ItemPedidoRequest item = novoItem(idProduto, 4);

        assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criar(
                        idUnidade,
                        CanalPedido.APP,
                        List.of(item),
                        null
                )
        );

        assertEquals(0, estoque.getQuantidadeReservada());
        verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
        verify(movimentacaoEstoqueRepository, never()).saveAll(anyList());
    }

    @Test
    void deveRejeitarProdutoForaDoCardapioAtivo() {
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        ItemPedidoRequest item = novoItem(idProduto, 1);

        when(unidadeRepository.findById(idUnidade)).thenReturn(Optional.of(unidade));
        when(unidade.getStatusUnidade()).thenReturn(StatusUnidade.ATIVA);
        when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
        when(produto.getIdProduto()).thenReturn(idProduto);
        when(produto.getStatusProduto()).thenReturn(StatusProduto.ATIVO);
        when(itemCardapioRepository
                .findByCardapio_Unidade_IdUnidadeAndCardapio_StatusCardapioAndProduto_IdProdutoAndStatusItem(
                        idUnidade,
                        StatusCardapio.ATIVO,
                        idProduto,
                        StatusItemCardapio.DISPONIVEL
                )).thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criar(
                        idUnidade,
                        CanalPedido.WEB,
                        List.of(item),
                        null
                )
        );

        verify(estoqueProdutoRepository, never())
                .buscarParaAtualizacao(any(), any());
        verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
    }

    private EstoqueProduto prepararFluxoValido(
            UUID idUnidade,
            UUID idProduto,
            int quantidadeDisponivel
    ) {
        EstoqueProduto estoque = new EstoqueProduto(
                unidade,
                produto,
                quantidadeDisponivel,
                0
        );

        when(unidadeRepository.findById(idUnidade)).thenReturn(Optional.of(unidade));
        when(unidade.getStatusUnidade()).thenReturn(StatusUnidade.ATIVA);
        when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
        when(produto.getIdProduto()).thenReturn(idProduto);
        lenient().when(produto.getNome()).thenReturn("Cuscuz Tradicional");
        when(produto.getStatusProduto()).thenReturn(StatusProduto.ATIVO);
        when(itemCardapioRepository
                .findByCardapio_Unidade_IdUnidadeAndCardapio_StatusCardapioAndProduto_IdProdutoAndStatusItem(
                        idUnidade,
                        StatusCardapio.ATIVO,
                        idProduto,
                        StatusItemCardapio.DISPONIVEL
                )).thenReturn(Optional.of(itemCardapio));
        when(itemCardapio.estaDisponivelEm(any())).thenReturn(true);
        lenient().when(itemCardapio.getPreco()).thenReturn(new BigDecimal("18.90"));
        when(estoqueProdutoRepository.buscarParaAtualizacao(idUnidade, idProduto))
                .thenReturn(Optional.of(estoque));
        return estoque;
    }

    private ItemPedidoRequest novoItem(UUID idProduto, int quantidade) {
        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setIdProduto(idProduto);
        item.setQuantidade(quantidade);
        return item;
    }
}
