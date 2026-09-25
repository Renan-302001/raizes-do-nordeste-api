package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.PedidoService;
import br.com.raizesdonordeste.api.controller.dto.CriarPedidoRequest;
import br.com.raizesdonordeste.api.controller.dto.PaginaResponse;
import br.com.raizesdonordeste.api.controller.dto.PedidoResponse;
import br.com.raizesdonordeste.api.controller.dto.PedidoResumoResponse;
import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import br.com.raizesdonordeste.api.domain.model.Pedido;
import br.com.raizesdonordeste.api.domain.model.StatusPedido;
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
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
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
    public PedidoResponse consultar(@PathVariable UUID idPedido) {
        return new PedidoResponse(pedidoService.consultar(idPedido));
    }

    @GetMapping
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
}
