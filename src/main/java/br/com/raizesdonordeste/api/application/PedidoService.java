package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.application.exception.AcessoNegadoException;
import br.com.raizesdonordeste.api.controller.dto.ItemPedidoRequest;
import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Cliente;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.Funcionario;
import br.com.raizesdonordeste.api.domain.model.HistoricoStatusPedido;
import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.ItemPedido;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import br.com.raizesdonordeste.api.domain.model.OrigemAlteracaoPedido;
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
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.HistoricoStatusPedidoRepository;
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
import java.util.Comparator;
import java.util.Set;
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
    private final HistoricoStatusPedidoRepository historicoStatusPedidoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UnidadeRepository unidadeRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository,
            ItemCardapioRepository itemCardapioRepository,
            EstoqueProdutoRepository estoqueProdutoRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            HistoricoStatusPedidoRepository historicoStatusPedidoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemCardapioRepository = itemCardapioRepository;
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.historicoStatusPedidoRepository = historicoStatusPedidoRepository;
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
        registrarHistorico(
                pedidoSalvo,
                usuario,
                null,
                StatusPedido.AGUARDANDO_PAGAMENTO,
                OrigemAlteracaoPedido.CRIACAO,
                "Pedido criado."
        );
        return pedidoSalvo;
    }

    @Transactional
    public Pedido atualizarStatus(
            UUID idPedido,
            StatusPedido novoStatus,
            UUID idUsuarioResponsavel,
            Set<String> perfis,
            String observacao
    ) {
        Pedido pedido = buscarPedidoParaAtualizacao(idPedido);
        Usuario usuario = buscarUsuarioObrigatorio(idUsuarioResponsavel);
        validarEscopoDaUnidade(usuario, pedido, perfis);
        StatusPedido statusAnterior = pedido.getStatusPedido();

        try {
            switch (novoStatus) {
                case EM_PREPARO -> {
                    exigirPerfil(perfis, "COZINHA", "Somente a cozinha pode iniciar o preparo.");
                    pedido.iniciarPreparo();
                }
                case PRONTO -> {
                    exigirPerfil(perfis, "COZINHA", "Somente a cozinha pode marcar o pedido como pronto.");
                    pedido.marcarComoPronto();
                }
                case ENTREGUE -> {
                    exigirPerfil(perfis, "ATENDENTE", "Somente o atendimento pode registrar a entrega.");
                    pedido.registrarEntrega();
                }
                default -> throw new RegraNegocioException(
                        "O status informado não é uma transição operacional permitida."
                );
            }
        } catch (IllegalStateException exception) {
            throw new RegraNegocioException(exception.getMessage());
        }

        registrarHistorico(
                pedido,
                usuario,
                statusAnterior,
                pedido.getStatusPedido(),
                OrigemAlteracaoPedido.OPERACAO,
                observacao
        );
        pedido.getItens().size();
        return pedido;
    }

    @Transactional
    public Pedido cancelar(
            UUID idPedido,
            UUID idUsuarioResponsavel,
            Set<String> perfis,
            String motivo
    ) {
        Pedido pedido = buscarPedidoParaAtualizacao(idPedido);
        Usuario usuario = buscarUsuarioObrigatorio(idUsuarioResponsavel);
        validarPermissaoDeCancelamento(usuario, pedido, perfis);
        validarEscopoDaUnidade(usuario, pedido, perfis);
        StatusPedido statusAnterior = pedido.getStatusPedido();

        if (statusAnterior != StatusPedido.AGUARDANDO_PAGAMENTO
                && statusAnterior != StatusPedido.PAGAMENTO_RECUSADO) {
            throw new RegraNegocioException(
                    "Pedidos pagos exigem um fluxo de estorno antes do cancelamento."
            );
        }

        liberarReservas(pedido, usuario);
        try {
            pedido.cancelar();
        } catch (IllegalStateException exception) {
            throw new RegraNegocioException(exception.getMessage());
        }

        registrarHistorico(
                pedido,
                usuario,
                statusAnterior,
                StatusPedido.CANCELADO,
                OrigemAlteracaoPedido.CANCELAMENTO,
                motivo
        );
        pedido.getItens().size();
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<HistoricoStatusPedido> listarHistorico(UUID idPedido) {
        if (!pedidoRepository.existsById(idPedido)) {
            throw new RecursoNaoEncontradoException("Pedido não encontrado.");
        }
        return historicoStatusPedidoRepository
                .findByPedido_IdPedidoOrderByAlteradoEmAsc(idPedido);
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

    private Usuario buscarUsuarioObrigatorio(UUID idUsuario) {
        if (idUsuario == null) {
            throw new AcessoNegadoException("Usuário autenticado não identificado.");
        }
        return buscarUsuarioOpcional(idUsuario);
    }

    private Pedido buscarPedidoParaAtualizacao(UUID idPedido) {
        return pedidoRepository.buscarParaProcessamentoPagamento(idPedido)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pedido não encontrado."
                ));
    }

    private void exigirPerfil(
            Set<String> perfis,
            String perfilNecessario,
            String mensagem
    ) {
        if (!possuiPerfil(perfis, perfilNecessario)
                && !possuiPerfil(perfis, "ADMIN_MATRIZ")) {
            throw new AcessoNegadoException(mensagem);
        }
    }

    private boolean possuiPerfil(Set<String> perfis, String perfil) {
        return perfis != null && perfis.contains(perfil);
    }

    private void validarEscopoDaUnidade(
            Usuario usuario,
            Pedido pedido,
            Set<String> perfis
    ) {
        if (possuiPerfil(perfis, "ADMIN_MATRIZ")) {
            return;
        }
        if (usuario instanceof Funcionario funcionario
                && !funcionario.getUnidade().getIdUnidade()
                .equals(pedido.getUnidade().getIdUnidade())) {
            throw new AcessoNegadoException(
                    "O funcionário não pertence à unidade deste pedido."
            );
        }
    }

    private void validarPermissaoDeCancelamento(
            Usuario usuario,
            Pedido pedido,
            Set<String> perfis
    ) {
        boolean perfilOperacional = possuiPerfil(perfis, "ATENDENTE")
                || possuiPerfil(perfis, "GERENTE")
                || possuiPerfil(perfis, "ADMIN_MATRIZ");
        if (perfilOperacional) {
            return;
        }

        if (!possuiPerfil(perfis, "CLIENTE")
                || pedido.getCliente() == null
                || !pedido.getCliente().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new AcessoNegadoException(
                    "O cliente só pode cancelar os próprios pedidos."
            );
        }
    }

    private void liberarReservas(Pedido pedido, Usuario usuario) {
        List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();
        pedido.getItens().stream()
                .sorted(Comparator.comparing(
                        (ItemPedido item) -> item.getProduto().getIdProduto()
                ))
                .forEach(item -> {
                    EstoqueProduto estoque = buscarEstoqueParaAtualizacao(
                            pedido.getUnidade().getIdUnidade(),
                            item.getProduto().getIdProduto()
                    );
                    int disponivelAnterior = estoque.getQuantidadeDisponivel();
                    int reservadoAnterior = estoque.getQuantidadeReservada();
                    try {
                        estoque.liberarReserva(item.getQuantidade());
                    } catch (IllegalArgumentException | IllegalStateException exception) {
                        throw new RegraNegocioException(exception.getMessage());
                    }
                    movimentacoes.add(new MovimentacaoEstoque(
                            estoque,
                            pedido,
                            usuario,
                            TipoMovimentacaoEstoque.LIBERACAO_RESERVA,
                            item.getQuantidade(),
                            disponivelAnterior,
                            estoque.getQuantidadeDisponivel(),
                            reservadoAnterior,
                            estoque.getQuantidadeReservada(),
                            "Liberação por cancelamento do pedido"
                    ));
                });
        movimentacaoEstoqueRepository.saveAll(movimentacoes);
    }

    private void registrarHistorico(
            Pedido pedido,
            Usuario usuario,
            StatusPedido anterior,
            StatusPedido novo,
            OrigemAlteracaoPedido origem,
            String observacao
    ) {
        historicoStatusPedidoRepository.save(new HistoricoStatusPedido(
                pedido,
                usuario,
                anterior,
                novo,
                origem,
                observacao
        ));
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
