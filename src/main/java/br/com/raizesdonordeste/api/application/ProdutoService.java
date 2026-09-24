package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.CadastroDuplicadoException;
import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.domain.model.Produto;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto cadastrar(
            String codigoPublico,
            String nome,
            String descricao,
            String categoria,
            String imagemUrl
    ) {
        String codigoNormalizado = codigoPublico.trim().toUpperCase(Locale.ROOT);

        if (produtoRepository.existsByCodigoPublico(codigoNormalizado)) {
            throw new CadastroDuplicadoException(
                    "O código público informado já está cadastrado."
            );
        }

        Produto produto = new Produto(
                codigoNormalizado,
                nome.trim(),
                descricao == null ? null : descricao.trim(),
                categoria.trim(),
                imagemUrl == null ? null : imagemUrl.trim()
        );

        return produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public Page<Produto> listar(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Produto buscar(UUID idProduto) {
        return produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado."
                ));
    }
}
