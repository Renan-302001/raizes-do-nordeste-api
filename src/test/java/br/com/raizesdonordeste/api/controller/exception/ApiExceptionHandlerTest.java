package br.com.raizesdonordeste.api.controller.exception;

import br.com.raizesdonordeste.api.application.exception.RecursoNaoEncontradoException;
import br.com.raizesdonordeste.api.application.exception.RegraNegocioException;
import br.com.raizesdonordeste.api.application.exception.CredenciaisInvalidasException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    @Test
    void deveRetornar404ParaRecursoNaoEncontrado() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                "GET",
                "/api/v1/produtos/inexistente"
        );

        ApiExceptionHandler handler = new ApiExceptionHandler();
        var resposta = handler.tratarRecursoNaoEncontrado(
                new RecursoNaoEncontradoException("Produto não encontrado."),
                request
        );

        assertEquals(404, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals("RESOURCE_NOT_FOUND", resposta.getBody().getCode());
        assertEquals("Produto não encontrado.", resposta.getBody().getMessage());
    }

    @Test
    void deveRetornar409ParaViolacaoDeRegraDeNegocio() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/v1/unidades/1/estoques/1/movimentacoes"
        );

        ApiExceptionHandler handler = new ApiExceptionHandler();
        var resposta = handler.tratarRegraNegocio(
                new RegraNegocioException("Estoque insuficiente."),
                request
        );

        assertEquals(409, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals("BUSINESS_RULE_VIOLATION", resposta.getBody().getCode());
        assertEquals("Estoque insuficiente.", resposta.getBody().getMessage());
    }

    @Test
    void deveRetornar409ParaEmailDuplicadoNoBanco() {
        SQLException erroSql = new SQLException(
                "Mensagem técnica de teste",
                "23505"
        );

        ConstraintViolationException violacao =
                new ConstraintViolationException(
                        "Restrição violada",
                        erroSql,
                        "usuario_email_key"
                );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Falha ao salvar",
                        violacao
                );

        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/v1/auth/cadastro"
        );

        ApiExceptionHandler handler = new ApiExceptionHandler();
        var resposta = handler.tratarIntegridade(exception, request);

        assertEquals(409, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals("CADASTRO_DUPLICADO", resposta.getBody().getCode());
        assertEquals(
                "O e-mail informado já está cadastrado.",
                resposta.getBody().getMessage()
        );
        assertEquals(
                "/api/v1/auth/cadastro",
                resposta.getBody().getPath()
        );
    }
    @Test
    void deveRetornar409ParaCpfDuplicado() {

        SQLException erroSql = new SQLException(
                "Mensagem técnica de teste",
                "23505"
        );

        ConstraintViolationException violacao =
                new ConstraintViolationException(
                        "Restrição violada",
                        erroSql,
                        "cliente_cpf_key"
                );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Falha ao salvar",
                        violacao
                );

        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/v1/auth/cadastro"
        );

        ApiExceptionHandler handler = new ApiExceptionHandler();
        var resposta = handler.tratarIntegridade(exception, request);

        assertEquals(409, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals("CADASTRO_DUPLICADO", resposta.getBody().getCode());
        assertEquals(
                "O CPF informado já está cadastrado.",
                resposta.getBody().getMessage()
        );
        assertEquals(
                "/api/v1/auth/cadastro",
                resposta.getBody().getPath()
        );
    }

    @Test
    void deveRetornar500ParaRestricaoDesconhecida() {

        SQLException erroSql = new SQLException(
                "Mensagem técnica de teste",
                "23505"
        );

        ConstraintViolationException violacao =
                new ConstraintViolationException(
                        "Restrição violada",
                        erroSql,
                        "outra_restricao_key"
                );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Falha ao salvar",
                        violacao
                );

        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/v1/auth/cadastro"
        );

        ApiExceptionHandler handler = new ApiExceptionHandler();
        var resposta = handler.tratarIntegridade(exception, request);

        assertEquals(500, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals("INTERNAL_ERROR", resposta.getBody().getCode());
        assertEquals(
                "Não foi possível concluir a operação.",
                resposta.getBody().getMessage()
        );
        assertEquals(
                "/api/v1/auth/cadastro",
                resposta.getBody().getPath()
        );
    }

    @Test
    void deveRetornar401ParaCredenciaisInvalidas() {
        CredenciaisInvalidasException exception =
                new CredenciaisInvalidasException();

        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/v1/auth/login"
        );

        ApiExceptionHandler handler = new ApiExceptionHandler();
        var resposta = handler.tratarCredenciaisInvalidas(exception, request);

        assertEquals(401, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals(
                "CREDENCIAIS_INVALIDAS",
                resposta.getBody().getCode()
        );
        assertEquals(
                "E-mail ou senha inválidos.",
                resposta.getBody().getMessage()
        );
        assertEquals(
                "/api/v1/auth/login",
                resposta.getBody().getPath()
        );
        assertTrue(resposta.getBody().getDetails().isEmpty());
    }
}
