package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.PagamentoService;
import br.com.raizesdonordeste.api.controller.dto.PagamentoResponse;
import br.com.raizesdonordeste.api.controller.dto.SolicitarPagamentoRequest;
import br.com.raizesdonordeste.api.domain.model.Pagamento;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pedidos/{idPedido}/pagamentos")
    public ResponseEntity<PagamentoResponse> solicitar(
            @PathVariable UUID idPedido,
            @RequestHeader("Idempotency-Key") String chaveIdempotencia,
            @Valid @RequestBody SolicitarPagamentoRequest request
    ) {
        Pagamento pagamento = pagamentoService.solicitar(
                idPedido,
                chaveIdempotencia,
                request.getResultadoSimulado()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PagamentoResponse(pagamento));
    }

    @GetMapping("/pagamentos/{idPagamento}")
    public PagamentoResponse consultar(@PathVariable UUID idPagamento) {
        return new PagamentoResponse(pagamentoService.consultar(idPagamento));
    }
}
