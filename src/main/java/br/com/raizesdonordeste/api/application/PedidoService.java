package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.controller.dto.ItemPedidoRequest;
import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Cliente;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.domain.model.StatusCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusConta;
import br.com.raizesdonordeste.api.domain.model.StatusItemCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import br.com.raizesdonordeste.api.domain.model.StatusProduto;
import br.com.raizesdonordeste.api.domain.model.StatusUnidade;
import br.com.raizesdonordeste.api.domain.model.TipoMovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.domain.model.Usuario;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.EstoqueProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ItemCardapioRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.MovimentacaoEstoqueRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PedidoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UnidadeRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemCardapioRepository itemCardapioRepository;
    private final EstoqueProdutoRepository estoqueProdutoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UnidadeRepository unidadeRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository,
            ItemCardapioRepository itemCardapioRepository,
            EstoqueProdutoRepository estoqueProdutoRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemCardapioRepository = itemCardapioRepository;
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    @Transactional
    public Pedido criar(
            UUID idUnidade,
            CanalPedido canalPedido,
            List<ItemPedidoRequest> itens,
            UUID idUsuarioAutenticado
    ) {
        Unidade unidade = unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Unidade não encontrada."
                ));

        if (unidade.getStatusUnidade() != StatusUnidade.ATIVA) {
            throw new RegraNegocioException(
                    "A unidade não está disponível para receber pedidos."
            );
        }

        Usuario usuario = buscarUsuarioOpcional(idUsuarioAutenticado);
        Cliente cliente = usuario instanceof Cliente clienteAutenticado
                ? clienteAutenticado : null;
        Pedido pedido = new Pedido(unidade, cliente, usuario, canalPedido);
        List<ReservaEfetuada> reservas = new ArrayList<>();
        Instant agora = Instant.now();

        try {
            for (ItemPedidoRequest itemRequest : itens) {
                Produto produto = buscarProdutoAtivo(itemRequest.getIdProduto());
                ItemCardapio itemCardapio = buscarItemDisponivel(
                        idUnidade,
                        produto.getIdProduto(),
                        agora
                );
                EstoqueProduto estoque = buscarEstoqueParaAtualizacao(
                        idUnidade,
                        produto.getIdProduto()
                );

                int saldoDisponivelAnterior = estoque.getQuantidadeDisponivel();
                int saldoReservadoAnterior = estoque.getQuantidadeReservada();
                estoque.reservar(itemRequest.getQuantidade());
                pedido.adicionarItem(
                        produto,
                        itemRequest.getQuantidade(),
                        itemCardapio.getPreco()
                );
                reservas.add(new ReservaEfetuada(
                        estoque,
                        itemRequest.getQuantidade(),
                        saldoDisponivelAnterior,
                        saldoReservadoAnterior
                ));
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new RegraNegocioException(exception.getMessage());
        }

        Pedido pedidoSalvo = pedidoRepository.saveAndFlush(pedido);
        registrarReservas(pedidoSalvo, usuario, reservas);
        return pedidoSalvo;
    }

    @Transactional(readOnly = true)
    public Pedido consultar(UUID idPedido) {
        return pedidoRepository.findDetalhadoByIdPedido(idPedido)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pedido não encontrado."
                ));
    }

    @Transactional(readOnly = true)
    public Page<Pedido> listar(
            CanalPedido canalPedido,
            StatusPedido statusPedido,
            Pageable pageable
    ) {
        if (canalPedido != null && statusPedido != null) {
            return pedidoRepository.findByCanalPedidoAndStatusPedido(
                    canalPedido,
                    statusPedido,
                    pageable
            );
        }
        if (canalPedido != null) {
            return pedidoRepository.findByCanalPedido(canalPedido, pageable);
        }
        if (statusPedido != null) {
            return pedidoRepository.findByStatusPedido(statusPedido, pageable);
        }
        return pedidoRepository.findAll(pageable);
    }

    private Usuario buscarUsuarioOpcional(UUID idUsuario) {
        if (idUsuario == null) {
            return null;
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário autenticado não encontrado."
                ));

        if (usuario.getStatusConta() != StatusConta.ATIVA) {
            throw new RegraNegocioException("A conta do usuário não está ativa.");
        }
        return usuario;
    }

    private Produto buscarProdutoAtivo(UUID idProduto) {
        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado."
                ));

        if (produto.getStatusProduto() != StatusProduto.ATIVO) {
            throw new RegraNegocioException(
                    "O produto " + produto.getNome() + " não está ativo."
            );
        }
        return produto;
    }

    private ItemCardapio buscarItemDisponivel(
            UUID idUnidade,
            UUID idProduto,
            Instant agora
    ) {
        ItemCardapio item = itemCardapioRepository
                .findByCardapio_Unidade_IdUnidadeAndCardapio_StatusCardapioAndProduto_IdProdutoAndStatusItem(
                        idUnidade,
                        StatusCardapio.ATIVO,
                        idProduto,
                        StatusItemCardapio.DISPONIVEL
                )
                .orElseThrow(() -> new RegraNegocioException(
                        "O produto não está disponível no cardápio da unidade."
                ));

        if (!item.estaDisponivelEm(agora)) {
            throw new RegraNegocioException(
                    "O produto está fora do período de vigência no cardápio."
            );
        }
        return item;
    }

    private EstoqueProduto buscarEstoqueParaAtualizacao(
            UUID idUnidade,
            UUID idProduto
    ) {
        return estoqueProdutoRepository
                .buscarParaAtualizacao(idUnidade, idProduto)
                .orElseThrow(() -> new RegraNegocioException(
                        "O produto não possui estoque cadastrado nesta unidade."
                ));
    }

    private void registrarReservas(
            Pedido pedido,
            Usuario usuario,
            List<ReservaEfetuada> reservas
    ) {
        List<MovimentacaoEstoque> movimentacoes = reservas.stream()
                .map(reserva -> new MovimentacaoEstoque(
                        reserva.estoque(),
                        pedido,
                        usuario,
                        TipoMovimentacaoEstoque.RESERVA,
                        reserva.quantidade(),
                        reserva.saldoDisponivelAnterior(),
                        reserva.estoque().getQuantidadeDisponivel(),
                        reserva.saldoReservadoAnterior(),
                        reserva.estoque().getQuantidadeReservada(),
                        "Reserva para o pedido " + pedido.getIdPedido()
                ))
                .toList();

        movimentacaoEstoqueRepository.saveAll(movimentacoes);
    }

    private record ReservaEfetuada(
            EstoqueProduto estoque,
            int quantidade,
            int saldoDisponivelAnterior,
            int saldoReservadoAnterior
    ) {
    }
}
