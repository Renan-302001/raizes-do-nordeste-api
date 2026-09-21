package br.com.raizesdonordeste.api.application;


import br.com.raizesdonordeste.api.application.exception.CredenciaisInvalidasException;
import br.com.raizesdonordeste.api.domain.model.StatusConta;
import br.com.raizesdonordeste.api.domain.model.Usuario;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import br.com.raizesdonordeste.api.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Jwt autenticar(String email, String senha) {

        Usuario encontrado = usuarioRepository.findByEmail(email).orElseThrow(() -> new CredenciaisInvalidasException());

        if (!passwordEncoder.matches(senha, encontrado.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        if (encontrado.getStatusConta() != StatusConta.ATIVA) {
            throw new CredenciaisInvalidasException();
        }

        return jwtService.gerarToken(encontrado.getIdUsuario());
    }
}