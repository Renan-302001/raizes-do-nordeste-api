package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.PagamentoService;
import br.com.raizesdonordeste.api.controller.dto.PagamentoResponse;
import br.com.raizesdonordeste.api.controller.dto.SolicitarPagamentoRequest;
import br.com.raizesdonordeste.api.domain.model.Pagamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pagamentos", description = "Integração com o gateway de pagamento mock.")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pedidos/{idPedido}/pagamentos")
    @Operation(
            summary = "Processar pagamento mock",
            description = "Exige o header Idempotency-Key e perfil CLIENTE ou ATENDENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento processado e registrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissão"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pedido ou chave de idempotência em conflito"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
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
    @Operation(summary = "Consultar pagamento registrado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissão"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public PagamentoResponse consultar(@PathVariable UUID idPagamento) {
        return new PagamentoResponse(pagamentoService.consultar(idPagamento));
    }
}
