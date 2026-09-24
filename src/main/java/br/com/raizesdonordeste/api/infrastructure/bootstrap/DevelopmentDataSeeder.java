package br.com.raizesdonordeste.api.infrastructure.bootstrap;

import br.com.raizesdonordeste.api.domain.model.Funcionario;
import br.com.raizesdonordeste.api.domain.model.Perfil;
import br.com.raizesdonordeste.api.domain.model.TipoEscopo;
import br.com.raizesdonordeste.api.domain.model.Unidade;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.FuncionarioRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.PerfilRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UnidadeRepository;
import br.com.raizesdonordeste.api.infrastructure.persistence.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DevelopmentDataSeeder implements ApplicationRunner {

    public static final String ADMIN_EMAIL = "admin.matriz@raizes.local";
    public static final String CODIGO_UNIDADE = "RECIFE-CENTRO";
    public static final String CODIGO_PERFIL = "ADMIN_MATRIZ";

    private final PerfilRepository perfilRepository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;

    public DevelopmentDataSeeder(
            PerfilRepository perfilRepository,
            UnidadeRepository unidadeRepository,
            UsuarioRepository usuarioRepository,
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin-password}") String adminPassword
    ) {
        this.perfilRepository = perfilRepository;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "SEED_ADMIN_PASSWORD é obrigatória quando SEED_ENABLED=true."
            );
        }

        Perfil perfil = perfilRepository.findByCodigoPerfil(CODIGO_PERFIL)
                .orElseGet(() -> perfilRepository.save(new Perfil(
                        CODIGO_PERFIL,
                        "Administrador da Matriz",
                        "Administração geral da rede.",
                        TipoEscopo.REDE
                )));

        Unidade unidade = unidadeRepository.findByCodigoPublico(CODIGO_UNIDADE)
                .orElseGet(() -> unidadeRepository.save(new Unidade(
                        "Raízes do Nordeste - Recife Centro",
                        "12345678000199",
                        CODIGO_UNIDADE,
                        "Rua do Sol",
                        "100",
                        null,
                        "Santo Antônio",
                        "Recife",
                        "PE",
                        "50010000"
                )));

        if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        Funcionario administrador = new Funcionario(
                "Administrador Local",
                ADMIN_EMAIL,
                passwordEncoder.encode(adminPassword),
                LocalDate.of(1990, 1, 1),
                unidade,
                perfil,
                "ADMIN-LOCAL",
                LocalDate.now()
        );

        funcionarioRepository.save(administrador);
    }
}
