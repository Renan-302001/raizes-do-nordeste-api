package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EstoqueService {

    private final EstoqueProdutoRepository estoqueProdutoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstoqueService(
            EstoqueProdutoRepository estoqueProdutoRepository,
            MovimentacaoEstoqueRepository movimentacaoRepository,
            UnidadeRepository unidadeRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Page<EstoqueProduto> listar(UUID idUnidade, Pageable pageable) {
        exigirUnidade(idUnidade);
        return estoqueProdutoRepository.findByUnidade_IdUnidade(idUnidade, pageable);
    }

    @Transactional(readOnly = true)
    public EstoqueProduto consultar(UUID idUnidade, UUID idProduto) {
        return estoqueProdutoRepository
                .findByUnidade_IdUnidadeAndProduto_IdProduto(idUnidade, idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque do produto não encontrado para a unidade."
                ));
    }

    @Transactional
    public MovimentacaoEstoque movimentar(
            UUID idUnidade,
            UUID idProduto,
            UUID idUsuarioResponsavel,
            TipoMovimentacaoEstoque tipo,
            int quantidade,
            String motivo
    ) {
        if (tipo != TipoMovimentacaoEstoque.ENTRADA
                && tipo != TipoMovimentacaoEstoque.SAIDA) {
            throw new RegraNegocioException(
                    "A movimentação manual deve ser do tipo ENTRADA ou SAIDA."
            );
        }

        Unidade unidade = exigirUnidade(idUnidade);
        Produto produto = exigirProduto(idProduto);
        Usuario responsavel = usuarioRepository.findById(idUsuarioResponsavel)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário responsável não encontrado."
                ));

        EstoqueProduto estoque = estoqueProdutoRepository
                .buscarParaAtualizacao(idUnidade, idProduto)
                .orElseGet(() -> criarEstoqueParaEntrada(
                        tipo,
                        unidade,
                        produto
                ));

        int disponivelAnterior = estoque.getQuantidadeDisponivel();
        int reservadoAnterior = estoque.getQuantidadeReservada();

        try {
            if (tipo == TipoMovimentacaoEstoque.ENTRADA) {
                estoque.adicionar(quantidade);
            } else {
                estoque.remover(quantidade);
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new RegraNegocioException(exception.getMessage());
        }

        estoqueProdutoRepository.saveAndFlush(estoque);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque(
                estoque,
                null,
                responsavel,
                tipo,
                quantidade,
                disponivelAnterior,
                estoque.getQuantidadeDisponivel(),
                reservadoAnterior,
                estoque.getQuantidadeReservada(),
                motivo
        );

        return movimentacaoRepository.save(movimentacao);
    }

    private EstoqueProduto criarEstoqueParaEntrada(
            TipoMovimentacaoEstoque tipo,
            Unidade unidade,
            Produto produto
    ) {
        if (tipo != TipoMovimentacaoEstoque.ENTRADA) {
            throw new RecursoNaoEncontradoException(
                    "Estoque do produto não encontrado para a unidade."
            );
        }

        return new EstoqueProduto(unidade, produto, 0, 0);
    }

    private Unidade exigirUnidade(UUID idUnidade) {
        return unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Unidade não encontrada."
                ));
    }

    private Produto exigirProduto(UUID idProduto) {
        return produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado."
                ));
    }
}
