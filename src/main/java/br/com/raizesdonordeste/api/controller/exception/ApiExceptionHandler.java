package br.com.raizesdonordeste.api.controller.exception;


import br.com.raizesdonordeste.api.application.exception.CadastroDuplicadoException;
import br.com.raizesdonordeste.api.application.exception.CredenciaisInvalidasException;
import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.application.exception.SenhaInvalidaException;
import br.com.raizesdonordeste.api.controller.dto.ErroCampoResponse;
import br.com.raizesdonordeste.api.controller.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CadastroDuplicadoException.class)
    public ResponseEntity<ErroResponse> tratarCadastroDuplicado(
            CadastroDuplicadoException exception,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "CADASTRO_DUPLICADO",
                exception.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ErroCampoResponse> detalhes = new ArrayList<>();

        for (FieldError erroCampo : exception.getBindingResult().getFieldErrors()) {
            detalhes.add(new ErroCampoResponse(
                    erroCampo.getField(),
                    erroCampo.getDefaultMessage()
            ));
        }
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                422,
                "VALIDATION_ERROR",
                "Existem campos inválidos.",
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                detalhes
        );
        return ResponseEntity.status(422).body(erro);
    }
    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarSenhaInvalida(
            SenhaInvalidaException exception,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                422,
                "SENHA_INVALIDA",
                exception.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );
        return ResponseEntity.status(422).body(erro);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoInvalido(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                400,
                "INVALID_REQUEST_BODY",
                "O corpo da requisição está inválido. Verifique o JSON e os formatos dos campos.",
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );
        return ResponseEntity.status(400).body(erro);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarIntegridade(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        Throwable causa = exception;

        while (causa != null) {
            if (causa instanceof ConstraintViolationException violacao
                    && "23505".equals(violacao.getSQLState())) {

                String restricao = violacao.getConstraintName();

                if ("usuario_email_key".equals(restricao)) {
                    return tratarCadastroDuplicado(
                            new CadastroDuplicadoException(
                                    "O e-mail informado já está cadastrado."
                            ),
                            request
                    );
                }

                if ("cliente_cpf_key".equals(restricao)) {
                    return tratarCadastroDuplicado(
                            new CadastroDuplicadoException(
                                    "O CPF informado já está cadastrado."
                            ),
                            request
                    );
                }
            }

            causa = causa.getCause();
        }

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                500,
                "INTERNAL_ERROR",
                "Não foi possível concluir a operação.",
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );

        return ResponseEntity.status(500).body(erro);

    }
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarCredenciaisInvalidas(
            CredenciaisInvalidasException exception,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                401,
                "CREDENCIAIS_INVALIDAS",
                exception.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(
            RegraNegocioException exception,
            HttpServletRequest request
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "BUSINESS_RULE_VIOLATION",
                exception.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }



}
