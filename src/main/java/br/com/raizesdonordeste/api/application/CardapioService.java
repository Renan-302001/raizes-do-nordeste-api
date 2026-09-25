package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.CadastroDuplicadoException;
import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.domain.model.Cardapio;
import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.domain.model.StatusCardapio;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.CardapioRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ItemCardapioRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ProdutoRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UnidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final ItemCardapioRepository itemCardapioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;

    public CardapioService(
            CardapioRepository cardapioRepository,
            ItemCardapioRepository itemCardapioRepository,
            UnidadeRepository unidadeRepository,
            ProdutoRepository produtoRepository
    ) {
        this.cardapioRepository = cardapioRepository;
        this.itemCardapioRepository = itemCardapioRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Cardapio cadastrar(UUID idUnidade, String nome, boolean ativo) {
        Unidade unidade = unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Unidade não encontrada."
                ));

        if (ativo && cardapioRepository
                .findByUnidade_IdUnidadeAndStatusCardapio(
                        idUnidade,
                        StatusCardapio.ATIVO
                ).isPresent()) {
            throw new RegraNegocioException(
                    "A unidade já possui um cardápio ativo."
            );
        }

        Cardapio cardapio = new Cardapio(unidade, nome.trim());
        if (ativo) {
            cardapio.ativar();
        }

        return cardapioRepository.save(cardapio);
    }

    @Transactional
    public ItemCardapio adicionarItem(
            UUID idCardapio,
            UUID idProduto,
            BigDecimal preco,
            Instant inicioVigencia,
            Instant fimVigencia
    ) {
        Cardapio cardapio = cardapioRepository.findById(idCardapio)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cardápio não encontrado."
                ));

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado."
                ));

        if (itemCardapioRepository
                .existsByCardapio_IdCardapioAndProduto_IdProduto(
                        idCardapio,
                        idProduto
                )) {
            throw new CadastroDuplicadoException(
                    "O produto já pertence a este cardápio."
            );
        }

        if (inicioVigencia != null
                && fimVigencia != null
                && !fimVigencia.isAfter(inicioVigencia)) {
            throw new RegraNegocioException(
                    "O fim da vigência deve ser posterior ao início."
            );
        }

        ItemCardapio item = new ItemCardapio(
                cardapio,
                produto,
                preco,
                inicioVigencia,
                fimVigencia
        );

        return itemCardapioRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Cardapio buscarAtivo(UUID idUnidade) {
        return cardapioRepository
                .findByUnidade_IdUnidadeAndStatusCardapio(
                        idUnidade,
                        StatusCardapio.ATIVO
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cardápio ativo não encontrado para a unidade."
                ));
    }

    @Transactional(readOnly = true)
    public List<ItemCardapio> listarItens(UUID idCardapio) {
        return itemCardapioRepository
                .findByCardapio_IdCardapioOrderByProduto_Nome(idCardapio);
    }
}
