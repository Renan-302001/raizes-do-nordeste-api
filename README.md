# API Raízes do Nordeste

Back-end desenvolvido para o estudo de caso da rede **Raízes do Nordeste**. A API implementa um fluxo completo de pedido com persistência, reserva e baixa de estoque, pagamento externo simulado, atualização de status e auditoria do histórico do pedido.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web, Spring Data JPA e Spring Security
- JWT para autenticação e autorização
- PostgreSQL
- Flyway para versionamento do banco
- Springdoc OpenAPI/Swagger UI
- JUnit 5 e Mockito
- Postman para testes manuais da API

## Organização do projeto

O código segue uma separação simplificada por responsabilidades:

- `domain`: entidades, enums e regras do negócio;
- `application`: serviços que coordenam os casos de uso;
- `controller`: endpoints e contratos HTTP de entrada e saída;
- `infrastructure`: persistência, integrações, segurança e inicialização de dados;
- `config`: configuração de segurança, JWT e OpenAPI.

## Funcionalidades implementadas

- cadastro de cliente e autenticação por JWT;
- controle de acesso por perfis;
- consulta e cadastro de produtos;
- consulta de unidades;
- cardápio ativo por unidade e inclusão de itens;
- entrada, saída e consulta de estoque por unidade;
- criação de pedido com `canalPedido` obrigatório;
- validação e reserva de estoque na criação do pedido;
- pagamento mock aprovado ou recusado, com chave de idempotência;
- baixa do estoque após aprovação do pagamento;
- atualização operacional do pedido: preparo, pronto e entregue;
- cancelamento de pedido ainda não pago e liberação das reservas;
- consulta e filtro de pedidos por canal e status;
- histórico de alterações de status;
- respostas de erro em formato padronizado.

## Pré-requisitos

- JDK 21;
- PostgreSQL em execução;
- Git;
- IntelliJ IDEA ou outro editor compatível com Maven;
- Postman, opcionalmente, para executar a coleção entregue.

O Maven Wrapper acompanha o repositório, portanto não é necessário instalar o Maven separadamente.

## Preparação do banco

No PostgreSQL, crie o usuário e o banco usados pela aplicação. A senha abaixo é apenas ilustrativa e deve ser substituída por uma senha local segura.

```sql
CREATE USER raizes_app WITH PASSWORD 'defina_uma_senha_local';
CREATE DATABASE raizes_nordeste OWNER raizes_app;
```

As tabelas não precisam ser criadas manualmente. Na primeira inicialização, o Flyway executa automaticamente as migrations existentes em `src/main/resources/db/migration`.

## Variáveis de ambiente

Use `.env.example` como referência. O arquivo contém apenas valores demonstrativos e pode ser copiado para uma configuração local que não seja versionada.

| Variável | Obrigatória | Finalidade |
|---|---:|---|
| `DB_URL` | Não | URL do PostgreSQL. Há um valor local padrão. |
| `DB_USERNAME` | Não | Usuário do banco. O padrão é `raizes_app`. |
| `DB_PASSWORD` | Sim | Senha local do usuário do banco. |
| `JWT_SECRET` | Sim | Chave Base64 de pelo menos 32 bytes para assinar os tokens. |
| `SERVER_PORT` | Não | Porta HTTP. O padrão é `8080`. |
| `SEED_ENABLED` | Não | Use `true` para criar os dados iniciais de desenvolvimento. |
| `SEED_ADMIN_PASSWORD` | Quando o seed estiver ativo | Senha local do administrador inicial. |

Uma chave JWT pode ser gerada no PowerShell sem registrar a chave no repositório:

```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
[Convert]::ToBase64String($bytes)
```

No IntelliJ IDEA, informe as variáveis em **Run > Edit Configurations > Environment variables**. Não inclua senhas ou chaves reais em commits.

## Execução

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Com o seed habilitado, a aplicação cria os perfis básicos e o administrador `admin.matriz@raizes.local`. A senha será exatamente a definida em `SEED_ADMIN_PASSWORD`.

## Documentação da API

Com a aplicação em execução:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- especificação OpenAPI em JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

No Swagger, use o botão **Authorize** e informe somente o token JWT retornado pelo login. O prefixo `Bearer` é aplicado pelo próprio Swagger.

## Recursos principais da API

| Recurso | Base das rotas | Responsabilidade |
|---|---|---|
| Autenticação | `/api/v1/auth` | Cadastro e login |
| Unidades | `/api/v1/unidades` | Consulta das unidades |
| Produtos | `/api/v1/produtos` | Catálogo de produtos |
| Cardápios | `/api/v1/unidades/{id}/cardapios` | Cardápio e disponibilidade por unidade |
| Estoque | `/api/v1/unidades/{id}/estoques` | Saldo e movimentações locais |
| Pedidos | `/api/v1/pedidos` | Criação, consulta, operação e cancelamento |
| Pagamentos | `/api/v1/pedidos/{id}/pagamentos` | Solicitação ao gateway mock |

O contrato completo, incluindo requests, responses, permissões e códigos HTTP, está disponível no Swagger.

## Fluxo principal para demonstração

1. cadastrar ou autenticar um cliente;
2. autenticar o administrador de desenvolvimento;
3. cadastrar um produto e abastecer o estoque da unidade;
4. criar um cardápio e adicionar o produto;
5. criar um pedido informando unidade, itens e `canalPedido`;
6. solicitar o pagamento mock com uma chave de idempotência;
7. consultar o pagamento e o pedido atualizado;
8. mover o pedido por `EM_PREPARO`, `PRONTO` e `ENTREGUE`;
9. consultar o histórico de status.

O gateway mock também permite demonstrar pagamento recusado. A mesma chave de idempotência enviada novamente para a mesma operação retorna o resultado já registrado, evitando cobrança duplicada.

## Coleção Postman

Importe o arquivo:

`docs/postman/raizes-do-nordeste-entrega-final.postman_collection.json`

A coleção está organizada nas pastas **Auth**, **Estoque e Produtos**, **Pedidos**, **Pagamentos** e **Operação de Pedidos**. Os scripts armazenam automaticamente tokens e identificadores usados pelas requisições seguintes.

Antes da execução:

1. mantenha a API iniciada;
2. confira a variável `baseUrl`;
3. defina apenas localmente as senhas usadas nos logins;
4. execute as pastas na ordem apresentada;
5. o cadastro de cliente deve ser executado apenas uma vez para o mesmo e-mail; uma repetição correta retorna conflito `409`.

## Testes automatizados

No Windows:

```powershell
.\mvnw.cmd test
```

No Linux ou macOS:

```bash
./mvnw test
```

Os testes cobrem regras de domínio, serviços de aplicação, persistência, tratamento de erros e segurança. Eles usam a mesma configuração de banco definida pelas variáveis de ambiente; por isso o PostgreSQL precisa estar acessível durante a execução completa.

## Padrão de erro

As falhas da API seguem uma estrutura única. Exemplo de validação:

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 422,
  "code": "VALIDATION_ERROR",
  "message": "Existem campos inválidos.",
  "path": "/api/v1/pedidos",
  "correlationId": "00000000-0000-0000-0000-000000000002",
  "details": [
    {
      "field": "canalPedido",
      "message": "O canal do pedido é obrigatório."
    }
  ]
}
```

Os retornos `401` e `403` também usam esse padrão.

## Segurança e privacidade

- senhas armazenadas exclusivamente com hash BCrypt;
- autenticação stateless por JWT;
- autorização por perfil em rotas administrativas e operacionais;
- resposta de cadastro nunca expõe o hash da senha;
- identificador de correlação nas respostas de erro;
- histórico para rastrear mudanças de status do pedido;
- segredos e credenciais mantidos fora do repositório.

O modelo completo prevê consentimento, fidelidade, campanhas e auditoria ampliada. Esses módulos permanecem documentados como evoluções do sistema e não fazem parte do fluxo técnico implementado neste MVP.

## Estado da entrega

O MVP implementado fecha o fluxo **Pedido → Pagamento mock → Atualização de status**, com persistência real em PostgreSQL. Estorno, programa de fidelidade e campanhas foram modelados conceitualmente, mas não implementados nesta versão por priorização do fluxo obrigatório.

Repositório: [github.com/Renan-302001/raizes-do-nordeste-api](https://github.com/Renan-302001/raizes-do-nordeste-api)
