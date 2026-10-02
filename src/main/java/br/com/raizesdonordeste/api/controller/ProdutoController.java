package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.ProdutoService;
import br.com.raizesdonordeste.api.controller.dto.CadastroProdutoRequest;
import br.com.raizesdonordeste.api.controller.dto.PaginaResponse;
import br.com.raizesdonordeste.api.controller.dto.ProdutoResponse;
import br.com.raizesdonordeste.api.domain.model.Produto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "Cadastro e consulta do catálogo de produtos.")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @Operation(summary = "Listar produtos")
    @SecurityRequirements
    public PaginaResponse<ProdutoResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtos = produtoService.listar(pageable);

        return PaginaResponse.from(produtos, ProdutoResponse::new);
    }

    @GetMapping("/{idProduto}")
    @Operation(summary = "Consultar produto por identificador")
    @SecurityRequirements
    public ProdutoResponse buscar(@PathVariable UUID idProduto) {
        return new ProdutoResponse(produtoService.buscar(idProduto));
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Requer perfil ADMIN_MATRIZ.")
    public ResponseEntity<ProdutoResponse> cadastrar(
            @Valid @RequestBody CadastroProdutoRequest request
    ) {
        Produto produto = produtoService.cadastrar(
                request.getCodigoPublico(),
                request.getNome(),
                request.getDescricao(),
                request.getCategoria(),
                request.getImagemUrl()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProdutoResponse(produto));
    }
}
