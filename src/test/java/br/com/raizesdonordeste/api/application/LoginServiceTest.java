package br.com.raizesdonordeste.api.application;

import br.com.raizesdonordeste.api.application.exception.CredenciaisInvalidasException;
import br.com.raizesdonordeste.api.domain.model.StatusConta;
import br.com.raizesdonordeste.api.domain.model.Cliente;
import br.com.raizesdonordeste.api.domain.model.Funcionario;
import br.com.raizesdonordeste.api.domain.model.Perfil;
import br.com.raizesdonordeste.api.domain.model.StatusVinculo;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import br.com.raizesdonordeste.api.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Cliente usuario;

    @Mock
    private Funcionario funcionario;

    @Mock
    private Perfil perfil;

    @InjectMocks
    private LoginService loginService;

    @Mock
    private Jwt token;

    @Test
    void deveAutenticarQuandoCredenciaisSaoValidas() {
        String email = "cliente@example.com";
        String senha = "SenhaCorreta123!";
        String senhaHash = "{bcrypt}hash-simulado";
        UUID idUsuario = UUID.randomUUID();

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario));

        when(usuario.getSenhaHash()).thenReturn(senhaHash);

        when(passwordEncoder.matches(senha, senhaHash))
                .thenReturn(true);

        when(usuario.getStatusConta()).thenReturn(StatusConta.ATIVA);
        when(usuario.getIdUsuario()).thenReturn(idUsuario);

        when(jwtService.gerarToken(idUsuario, "CLIENTE")).thenReturn(token);

        Jwt resultado = loginService.autenticar(email, senha);

        assertSame(token, resultado);
        verify(jwtService).gerarToken(idUsuario, "CLIENTE");
    }
    @Test
    void deveRejeitarQuandoEmailNaoExiste() {
        String email = "naoexiste@example.com";
        String senha = "SenhaQualquer123!";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        CredenciaisInvalidasException exception = assertThrows(
                CredenciaisInvalidasException.class,
                () -> loginService.autenticar(email, senha)
        );

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void deveRejeitarQuandoSenhaEstaIncorreta() {
        String email = "cliente@example.com";
        String senha = "SenhaIncorreta123!";
        String senhaHash = "{bcrypt}hash-simulado";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario));

        when(usuario.getSenhaHash()).thenReturn(senhaHash);

        when(passwordEncoder.matches(senha, senhaHash))
                .thenReturn(false);

        CredenciaisInvalidasException exception = assertThrows(
                CredenciaisInvalidasException.class,
                () -> loginService.autenticar(email, senha)
        );

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verifyNoInteractions(jwtService);
    }

    @Test
    void deveRejeitarQuandoContaNaoEstaAtiva() {
        String email = "cliente@example.com";
        String senha = "SenhaCorreta123!";
        String senhaHash = "{bcrypt}hash-simulado";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario));

        when(usuario.getSenhaHash()).thenReturn(senhaHash);

        when(passwordEncoder.matches(senha, senhaHash))
                .thenReturn(true);

        when(usuario.getStatusConta()).thenReturn(StatusConta.INATIVA);

        CredenciaisInvalidasException exception = assertThrows(
                CredenciaisInvalidasException.class,
                () -> loginService.autenticar(email, senha)
        );

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verifyNoInteractions(jwtService);
    }

    @Test
    void deveGerarTokenComPerfilDoFuncionarioAtivo() {
        String email = "admin@raizes.local";
        String senha = "SenhaCorreta123!";
        String senhaHash = "{bcrypt}hash-simulado";
        UUID idUsuario = UUID.randomUUID();

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(funcionario));
        when(funcionario.getSenhaHash()).thenReturn(senhaHash);
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);
        when(funcionario.getStatusConta()).thenReturn(StatusConta.ATIVA);
        when(funcionario.getStatusVinculo()).thenReturn(StatusVinculo.ATIVO);
        when(funcionario.getPerfil()).thenReturn(perfil);
        when(perfil.isAtivo()).thenReturn(true);
        when(perfil.getCodigoPerfil()).thenReturn("ADMIN_MATRIZ");
        when(funcionario.getIdUsuario()).thenReturn(idUsuario);
        when(jwtService.gerarToken(idUsuario, "ADMIN_MATRIZ"))
                .thenReturn(token);

        Jwt resultado = loginService.autenticar(email, senha);

        assertSame(token, resultado);
        verify(jwtService).gerarToken(idUsuario, "ADMIN_MATRIZ");
    }

}
