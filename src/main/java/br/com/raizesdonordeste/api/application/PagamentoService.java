package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.application.gateway.CenarioPagamentoMock;
import br.com.raizesdonordeste.api.application.gateway.PagamentoGateway;
import br.com.raizesdonordeste.api.application.gateway.ResultadoPagamentoGateway;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.ItemPedido;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Pagamento;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPagamento;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import br.com.raizesdonordeste.api.domain.model.TipoMovimentacaoEstoque;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.EstoqueProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.MovimentacaoEstoqueRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PagamentoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final EstoqueProdutoRepository estoqueProdutoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PagamentoGateway pagamentoGateway;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            EstoqueProdutoRepository estoqueProdutoRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            PagamentoGateway pagamentoGateway
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.pagamentoGateway = pagamentoGateway;
    }

    @Transactional
    public Pagamento solicitar(
            UUID idPedido,
            String chaveIdempotencia,
            CenarioPagamentoMock cenario
    ) {
        String chaveNormalizada = validarChave(chaveIdempotencia);
        Pagamento existente = pagamentoRepository
                .findByChaveIdempotencia(chaveNormalizada)
                .orElse(null);

        if (existente != null) {
            if (!existente.getPedido().getIdPedido().equals(idPedido)) {
                throw new RegraNegocioException(
                        "A chave de idempotência já foi usada em outro pedido."
                );
            }
            return existente;
        }

        Pedido pedido = pedidoRepository
                .buscarParaProcessamentoPagamento(idPedido)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pedido não encontrado."
                ));
        validarEstadoPedido(pedido);

        Pagamento pagamento = new Pagamento(
                pedido,
                chaveNormalizada,
                pedido.getValorTotal()
        );
        pagamentoRepository.saveAndFlush(pagamento);

        ResultadoPagamentoGateway resultado = pagamentoGateway.processar(
                idPedido,
                pedido.getValorTotal(),
                chaveNormalizada,
                cenario
        );
        aplicarResultado(pagamento, pedido, resultado);
        return pagamento;
    }

    @Transactional(readOnly = true)
    public Pagamento consultar(UUID idPagamento) {
        return pagamentoRepository.findDetalhadoByIdPagamento(idPagamento)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pagamento não encontrado."
                ));
    }

    private String validarChave(String chave) {
        if (chave == null || chave.isBlank()) {
            throw new RegraNegocioException(
                    "A chave de idempotência é obrigatória."
            );
        }
        String normalizada = chave.trim();
        if (normalizada.length() > 100) {
            throw new RegraNegocioException(
                    "A chave de idempotência deve ter no máximo 100 caracteres."
            );
        }
        return normalizada;
    }

    private void validarEstadoPedido(Pedido pedido) {
        if (pedido.getStatusPedido() != StatusPedido.AGUARDANDO_PAGAMENTO
                && pedido.getStatusPedido() != StatusPedido.PAGAMENTO_RECUSADO) {
            throw new RegraNegocioException(
                    "O pedido não aceita um novo pagamento no estado atual."
            );
        }
    }

    private void aplicarResultado(
            Pagamento pagamento,
            Pedido pedido,
            ResultadoPagamentoGateway resultado
    ) {
        try {
            if (resultado.status() == StatusPagamento.APROVADO) {
                pagamento.aprovar(
                        resultado.idTransacao(),
                        resultado.codigo(),
                        resultado.mensagem()
                );
                confirmarSaidaDoEstoque(pedido);
                pedido.registrarPagamentoAprovado();
                return;
            }

            if (resultado.status() == StatusPagamento.RECUSADO) {
                pagamento.recusar(
                        resultado.idTransacao(),
                        resultado.codigo(),
                        resultado.mensagem()
                );
                pedido.registrarPagamentoRecusado();
                return;
            }

            pagamento.registrarErro(
                    resultado.codigo(),
                    resultado.mensagem()
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new RegraNegocioException(exception.getMessage());
        }
    }

    private void confirmarSaidaDoEstoque(Pedido pedido) {
        List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();
        List<ItemPedido> itensOrdenados = pedido.getItens().stream()
                .sorted(Comparator.comparing(
                        (ItemPedido item) -> item.getProduto().getIdProduto()
                ))
                .toList();

        for (ItemPedido item : itensOrdenados) {
            EstoqueProduto estoque = estoqueProdutoRepository
                    .buscarParaAtualizacao(
                            pedido.getUnidade().getIdUnidade(),
                            item.getProduto().getIdProduto()
                    )
                    .orElseThrow(() -> new RegraNegocioException(
                            "Estoque do item do pedido não encontrado."
                    ));

            int disponivelAnterior = estoque.getQuantidadeDisponivel();
            int reservadoAnterior = estoque.getQuantidadeReservada();
            estoque.confirmarSaida(item.getQuantidade());
            movimentacoes.add(new MovimentacaoEstoque(
                    estoque,
                    pedido,
                    pedido.getUsuarioCriador(),
                    TipoMovimentacaoEstoque.BAIXA_PEDIDO,
                    item.getQuantidade(),
                    disponivelAnterior,
                    estoque.getQuantidadeDisponivel(),
                    reservadoAnterior,
                    estoque.getQuantidadeReservada(),
                    "Baixa após aprovação do pagamento"
            ));
        }

        movimentacaoEstoqueRepository.saveAll(movimentacoes);
    }
}
