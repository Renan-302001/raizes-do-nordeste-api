package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.gateway.CenarioPagamentoMock;
import br.com.raizesdonordeste.api.application.gateway.PagamentoGateway;
import br.com.raizesdonordeste.api.application.gateway.ResultadoPagamentoGateway;
import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Pagamento;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.domain.model.StatusPagamento;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.EstoqueProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.HistoricoStatusPedidoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.MovimentacaoEstoqueRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PagamentoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private EstoqueProdutoRepository estoqueProdutoRepository;
    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    @Mock
    private PagamentoGateway pagamentoGateway;
    @Mock
    private HistoricoStatusPedidoRepository historicoStatusPedidoRepository;
    @Mock
    private Unidade unidade;
    @Mock
    private Produto produto;

    @InjectMocks
    private PagamentoService pagamentoService;

    @Test
    void deveAprovarPagamentoEConfirmarSaidaDoEstoque() {
        UUID idPedido = UUID.randomUUID();
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        Pedido pedido = novoPedido(idUnidade, idProduto);
        EstoqueProduto estoque = novoEstoqueReservado();
        prepararPagamentoNovo(idPedido, pedido);

        when(pagamentoGateway.processar(
                eq(idPedido),
                eq(new BigDecimal("37.80")),
                eq("pagamento-001"),
                eq(CenarioPagamentoMock.APROVADO)
        )).thenReturn(new ResultadoPagamentoGateway(
                StatusPagamento.APROVADO,
                "mock-123",
                "00",
                "Pagamento aprovado."
        ));
        when(estoqueProdutoRepository.buscarParaAtualizacao(idUnidade, idProduto))
                .thenReturn(Optional.of(estoque));

        Pagamento pagamento = pagamentoService.solicitar(
                idPedido,
                "pagamento-001",
                CenarioPagamentoMock.APROVADO
        );

        assertEquals(StatusPagamento.APROVADO, pagamento.getStatusPagamento());
        assertEquals(StatusPedido.CONFIRMADO, pedido.getStatusPedido());
        assertEquals(8, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
        verify(movimentacaoEstoqueRepository).saveAll(anyList());
    }

    @Test
    void deveRecusarPagamentoEPreservarReserva() {
        UUID idPedido = UUID.randomUUID();
        UUID idUnidade = UUID.randomUUID();
        UUID idProduto = UUID.randomUUID();
        Pedido pedido = novoPedido(idUnidade, idProduto);
        EstoqueProduto estoque = novoEstoqueReservado();
        prepararPagamentoNovo(idPedido, pedido);

        when(pagamentoGateway.processar(
                eq(idPedido),
                eq(new BigDecimal("37.80")),
                eq("pagamento-002"),
                eq(CenarioPagamentoMock.RECUSADO)
        )).thenReturn(new ResultadoPagamentoGateway(
                StatusPagamento.RECUSADO,
                "mock-456",
                "51",
                "Pagamento recusado."
        ));

        Pagamento pagamento = pagamentoService.solicitar(
                idPedido,
                "pagamento-002",
                CenarioPagamentoMock.RECUSADO
        );

        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatusPagamento());
        assertEquals(StatusPedido.PAGAMENTO_RECUSADO, pedido.getStatusPedido());
        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(2, estoque.getQuantidadeReservada());
        verify(estoqueProdutoRepository, never())
                .buscarParaAtualizacao(any(), any());
        verify(movimentacaoEstoqueRepository, never()).saveAll(anyList());
    }

    @Test
    void deveRetornarPagamentoExistenteAoRepetirChave() {
        UUID idPedido = UUID.randomUUID();
        Pedido pedido = org.mockito.Mockito.mock(Pedido.class);
        Pagamento pagamento = org.mockito.Mockito.mock(Pagamento.class);

        when(pagamentoRepository.findByChaveIdempotencia("chave-repetida"))
                .thenReturn(Optional.of(pagamento));
        when(pagamento.getPedido()).thenReturn(pedido);
        when(pedido.getIdPedido()).thenReturn(idPedido);

        Pagamento resultado = pagamentoService.solicitar(
                idPedido,
                "chave-repetida",
                CenarioPagamentoMock.APROVADO
        );

        assertSame(pagamento, resultado);
        verify(pedidoRepository, never())
                .buscarParaProcessamentoPagamento(any());
        verify(pagamentoGateway, never())
                .processar(any(), any(), any(), any());
    }

    private Pedido novoPedido(UUID idUnidade, UUID idProduto) {
        lenient().when(unidade.getIdUnidade()).thenReturn(idUnidade);
        lenient().when(produto.getIdProduto()).thenReturn(idProduto);
        when(produto.getNome()).thenReturn("Cuscuz Tradicional");

        Pedido pedido = new Pedido(unidade, null, null, CanalPedido.TOTEM);
        pedido.adicionarItem(produto, 2, new BigDecimal("18.90"));
        return pedido;
    }

    private EstoqueProduto novoEstoqueReservado() {
        EstoqueProduto estoque = new EstoqueProduto(unidade, produto, 10, 0);
        estoque.reservar(2);
        return estoque;
    }

    private void prepararPagamentoNovo(UUID idPedido, Pedido pedido) {
        when(pagamentoRepository.findByChaveIdempotencia(any()))
                .thenReturn(Optional.empty());
        when(pedidoRepository.buscarParaProcessamentoPagamento(idPedido))
                .thenReturn(Optional.of(pedido));
        when(pagamentoRepository.saveAndFlush(any(Pagamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
