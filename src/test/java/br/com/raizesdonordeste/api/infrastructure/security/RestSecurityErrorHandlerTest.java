package br.com.raizesdonordeste.api.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestSecurityErrorHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveRetornarErroPadronizadoQuandoNaoAutenticado() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
                "GET",
                "/api/v1/pedidos"
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(objectMapper);

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException("Token ausente")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));
        assertEquals("UNAUTHENTICATED", body.get("code").asText());
        assertEquals("/api/v1/pedidos", body.get("path").asText());
        assertFalse(body.get("correlationId").asText().isBlank());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoAcessoForNegado() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/v1/produtos"
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        RestAccessDeniedHandler handler = new RestAccessDeniedHandler(objectMapper);

        handler.handle(
                request,
                response,
                new AccessDeniedException("Perfil sem permissão")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));
        assertEquals("ACCESS_DENIED", body.get("code").asText());
        assertEquals("/api/v1/produtos", body.get("path").asText());
        assertFalse(body.get("correlationId").asText().isBlank());
    }
}
