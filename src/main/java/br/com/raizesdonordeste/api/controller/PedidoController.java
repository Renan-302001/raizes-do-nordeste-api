package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.PedidoService;
import br.com.raizesdonordeste.api.controller.dto.CriarPedidoRequest;
import br.com.raizesdonordeste.api.controller.dto.AtualizarStatusPedidoRequest;
import br.com.raizesdonordeste.api.controller.dto.CancelarPedidoRequest;
import br.com.raizesdonordeste.api.controller.dto.HistoricoStatusPedidoResponse;
import br.com.raizesdonordeste.api.controller.dto.PaginaResponse;
import br.com.raizesdonordeste.api.controller.dto.PedidoResponse;
import br.com.raizesdonordeste.api.controller.dto.PedidoResumoResponse;
import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Criação, consulta e operação dos pedidos.")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Criar pedido e reservar estoque")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado e estoque reservado"),
            @ApiResponse(responseCode = "404", description = "Unidade ou item do cardápio não encontrado"),
            @ApiResponse(responseCode = "409", description = "Produto indisponível ou estoque insuficiente"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    public ResponseEntity<PedidoResponse> criar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CriarPedidoRequest request
    ) {
        UUID idUsuario = jwt == null ? null : UUID.fromString(jwt.getSubject());
        Pedido pedido = pedidoService.criar(
                request.getIdUnidade(),
                request.getCanalPedido(),
                request.getItens(),
                idUsuario
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PedidoResponse(pedido));
    }

    @GetMapping("/{idPedido}")
    @Operation(summary = "Consultar pedido por identificador")
    public PedidoResponse consultar(@PathVariable UUID idPedido) {
        return new PedidoResponse(pedidoService.consultar(idPedido));
    }

    @PatchMapping("/{idPedido}/status")
    @Operation(summary = "Atualizar status operacional do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissão"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "409", description = "Transição de status inválida")
    })
    public PedidoResponse atualizarStatus(
            @PathVariable UUID idPedido,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AtualizarStatusPedidoRequest request
    ) {
        return new PedidoResponse(pedidoService.atualizarStatus(
                idPedido,
                request.getNovoStatus(),
                UUID.fromString(jwt.getSubject()),
                extrairPerfis(jwt),
                request.getObservacao()
        ));
    }

    @PatchMapping("/{idPedido}/cancelar")
    @Operation(summary = "Cancelar pedido ainda não pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado e reservas liberadas"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissão"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pedido não pode mais ser cancelado")
    })
    public PedidoResponse cancelar(
            @PathVariable UUID idPedido,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody(required = false) CancelarPedidoRequest request
    ) {
        return new PedidoResponse(pedidoService.cancelar(
                idPedido,
                UUID.fromString(jwt.getSubject()),
                extrairPerfis(jwt),
                request == null ? null : request.getMotivo()
        ));
    }

    @GetMapping("/{idPedido}/historico-status")
    @Operation(summary = "Consultar histórico de status do pedido")
    public List<HistoricoStatusPedidoResponse> listarHistorico(
            @PathVariable UUID idPedido
    ) {
        return pedidoService.listarHistorico(idPedido).stream()
                .map(HistoricoStatusPedidoResponse::new)
                .toList();
    }

    @GetMapping
    @Operation(summary = "Listar pedidos com filtros de canal e status")
    public PaginaResponse<PedidoResumoResponse> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pedido> pedidos = pedidoService.listar(
                canalPedido,
                status,
                pageable
        );

        return PaginaResponse.from(pedidos, PedidoResumoResponse::new);
    }

    private Set<String> extrairPerfis(Jwt jwt) {
        List<String> perfis = jwt.getClaimAsStringList("roles");
        return perfis == null ? Set.of() : new HashSet<>(perfis);
    }
}
