package br.com.raizesdonordeste.api.controller;


import br.com.raizesdonordeste.api.application.CadastroClienteService;
import br.com.raizesdonordeste.api.controller.dto.CadastroClienteRequest;
import br.com.raizesdonordeste.api.controller.dto.CadastroClienteResponse;
import br.com.raizesdonordeste.api.domain.model.Cliente;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CadastroClienteService cadastroClienteService;

    public AuthController(CadastroClienteService cadastroClienteService) {
        this.cadastroClienteService = cadastroClienteService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroClienteResponse> cadastrar(
            @Valid @RequestBody CadastroClienteRequest request
            ) {
        Cliente cliente = cadastroClienteService.cadastrar(
                request.getNome(),
                request.getEmail(),
                request.getSenha(),
                request.getDataNascimento(),
                request.getCpf()
        );

        CadastroClienteResponse response = new CadastroClienteResponse(
                cliente.getIdUsuario(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getStatusConta(),
                cliente.getCriadoEm()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
