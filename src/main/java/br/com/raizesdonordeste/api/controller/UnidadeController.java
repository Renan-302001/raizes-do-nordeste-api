package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.UnidadeService;
import br.com.raizesdonordeste.api.controller.dto.PaginaResponse;
import br.com.raizesdonordeste.api.controller.dto.UnidadeResponse;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @GetMapping
    public PaginaResponse<UnidadeResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Unidade> unidades = unidadeService.listarAtivas(pageable);

        return PaginaResponse.from(unidades, UnidadeResponse::new);
    }

    @GetMapping("/{idUnidade}")
    public UnidadeResponse buscar(@PathVariable UUID idUnidade) {
        return new UnidadeResponse(unidadeService.buscar(idUnidade));
    }
}
