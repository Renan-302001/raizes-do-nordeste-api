package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.EstoqueService;
import br.com.raizesdonordeste.api.controller.dto.EstoqueProdutoResponse;
import br.com.raizesdonordeste.api.controller.dto.MovimentacaoEstoqueRequest;
import br.com.raizesdonordeste.api.controller.dto.MovimentacaoEstoqueResponse;
import br.com.raizesdonordeste.api.controller.dto.PaginaResponse;
import br.com.raizesdonordeste.api.domain.model.EstoqueProduto;
import br.com.raizesdonordeste.api.domain.model.MovimentacaoEstoque;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unidades/{idUnidade}/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public PaginaResponse<EstoqueProdutoResponse> listar(
            @PathVariable UUID idUnidade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EstoqueProduto> estoques = estoqueService.listar(idUnidade, pageable);

        return PaginaResponse.from(estoques, EstoqueProdutoResponse::new);
    }

    @GetMapping("/{idProduto}")
    public EstoqueProdutoResponse consultar(
            @PathVariable UUID idUnidade,
            @PathVariable UUID idProduto
    ) {
        return new EstoqueProdutoResponse(
                estoqueService.consultar(idUnidade, idProduto)
        );
    }

    @PostMapping("/{idProduto}/movimentacoes")
    public ResponseEntity<MovimentacaoEstoqueResponse> movimentar(
            @PathVariable UUID idUnidade,
            @PathVariable UUID idProduto,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MovimentacaoEstoqueRequest request
    ) {
        MovimentacaoEstoque movimentacao = estoqueService.movimentar(
                idUnidade,
                idProduto,
                UUID.fromString(jwt.getSubject()),
                request.getTipoMovimentacao(),
                request.getQuantidade(),
                request.getMotivo()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MovimentacaoEstoqueResponse(movimentacao));
    }
}
