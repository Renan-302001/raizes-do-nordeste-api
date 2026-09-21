package br.com.raizesdonordeste.api.controller;


import br.com.raizesdonordeste.api.application.CadastroClienteService;
import br.com.raizesdonordeste.api.application.LoginService;
import br.com.raizesdonordeste.api.controller.dto.CadastroClienteRequest;
import br.com.raizesdonordeste.api.controller.dto.CadastroClienteResponse;
import br.com.raizesdonordeste.api.controller.dto.LoginRequest;
import br.com.raizesdonordeste.api.controller.dto.LoginResponse;
import br.com.raizesdonordeste.api.domain.model.Cliente;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CadastroClienteService cadastroClienteService;
    private final LoginService loginService;

    public AuthController(CadastroClienteService cadastroClienteService, LoginService loginService) {
        this.cadastroClienteService = cadastroClienteService;
        this.loginService = loginService;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
            ) {
        Jwt token = loginService.autenticar(
                request.getEmail(),
                request.getSenha()
        );

        long expiresIn = Duration.between(
                token.getIssuedAt(),
                token.getExpiresAt()
        ).getSeconds();

        LoginResponse response = new LoginResponse(
                token.getTokenValue(),
                "Bearer",
                expiresIn
        );
        return ResponseEntity.ok(response);
    }
}
