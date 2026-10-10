# API Raízes do Nordeste

Este projeto foi desenvolvido para o estudo de caso da rede **Raízes do Nordeste**, na disciplina de Projeto Back-End.

O objetivo foi criar uma API que permita cadastrar clientes, consultar produtos e unidades, controlar o estoque e realizar pedidos. O fluxo principal implementado vai desde a criação do pedido até o pagamento simulado e a atualização do seu status.

## Tecnologias utilizadas

- Java 21;
- Spring Boot 4;
- Spring Web;
- Spring Data JPA;
- Spring Security;
- JWT;
- PostgreSQL;
- Flyway;
- Swagger/OpenAPI;
- JUnit 5 e Mockito;
- Postman.

## Organização do projeto

O projeto foi separado em pacotes de acordo com a responsabilidade de cada parte:

- `domain`: entidades, enums e regras de negócio;
- `application`: serviços que executam as operações da aplicação;
- `controller`: endpoints, dados de entrada e respostas da API;
- `infrastructure`: repositórios, integrações, segurança e dados iniciais;
- `config`: configurações de segurança, JWT e Swagger.

Essa separação foi utilizada para evitar que as regras de negócio ficassem misturadas com o acesso ao banco ou com os controllers.

## Funcionalidades implementadas

- cadastro de cliente;
- login e autenticação por JWT;
- controle de acesso por perfil;
- cadastro e consulta de produtos;
- consulta de unidades;
- criação de cardápio e inclusão de produtos;
- entrada, saída e consulta de estoque;
- criação de pedido com o campo `canalPedido`;
- reserva de estoque durante a criação do pedido;
- pagamento mock aprovado ou recusado;
- uso de chave de idempotência no pagamento;
- baixa do estoque depois do pagamento aprovado;
- atualização do pedido para `EM_PREPARO`, `PRONTO` e `ENTREGUE`;
- cancelamento de pedido ainda não pago;
- consulta de pedidos por canal e status;
- registro do histórico de status;
- respostas de erro padronizadas.

## Requisitos para executar

Antes de iniciar, é necessário ter instalado:

- JDK 21;
- PostgreSQL;
- Git;
- IntelliJ IDEA ou outra IDE compatível com Maven;
- Postman, caso seja utilizada a coleção de testes.

O projeto possui Maven Wrapper, portanto não é necessário instalar o Maven separadamente.

## Configuração do banco de dados

Com o PostgreSQL em execução, crie o usuário e o banco da aplicação. A senha apresentada abaixo é apenas um exemplo e deve ser substituída por uma senha local.

```sql
CREATE USER raizes_app WITH PASSWORD 'defina_uma_senha_local';
CREATE DATABASE raizes_nordeste OWNER raizes_app;
```

As tabelas não precisam ser criadas manualmente. O Flyway executa as migrations automaticamente quando a aplicação é iniciada.

Os arquivos das migrations estão em:

```text
src/main/resources/db/migration
```

## Variáveis de ambiente

O arquivo `.env.example` pode ser utilizado como referência. As variáveis necessárias são:

| Variável | Obrigatória | Utilização |
|---|---:|---|
| `DB_URL` | Não | Endereço do PostgreSQL. O projeto possui um valor local padrão. |
| `DB_USERNAME` | Não | Usuário do banco. O valor padrão é `raizes_app`. |
| `DB_PASSWORD` | Sim | Senha do usuário do banco. |
| `JWT_SECRET` | Sim | Chave Base64 com pelo menos 32 bytes para gerar os tokens. |
| `SERVER_PORT` | Não | Porta da aplicação. O valor padrão é `8080`. |
| `SEED_ENABLED` | Não | Deve receber `true` para criar os dados iniciais. |
| `SEED_ADMIN_PASSWORD` | Com o seed ativo | Senha do administrador inicial. |

Uma chave para o JWT pode ser gerada no PowerShell com os comandos abaixo:

```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
[Convert]::ToBase64String($bytes)
```

No IntelliJ IDEA, as variáveis podem ser adicionadas em **Run > Edit Configurations > Environment variables**.

As senhas e chaves utilizadas localmente não devem ser enviadas para o repositório.

## Como iniciar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Quando o seed está habilitado, a aplicação cria os perfis necessários e o administrador com o e-mail `admin.matriz@raizes.local`. A senha será o valor configurado em `SEED_ADMIN_PASSWORD`.

## Swagger

Depois de iniciar a aplicação, a documentação pode ser acessada nos endereços:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI em JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Para testar uma rota protegida pelo Swagger, primeiro realize o login. Em seguida, copie o token retornado e informe no botão **Authorize**. Não é necessário escrever `Bearer`, pois o Swagger adiciona esse prefixo.

## Principais rotas

| Recurso | Rota base | Finalidade |
|---|---|---|
| Autenticação | `/api/v1/auth` | Cadastro e login |
| Unidades | `/api/v1/unidades` | Consulta de unidades |
| Produtos | `/api/v1/produtos` | Cadastro e consulta de produtos |
| Cardápios | `/api/v1/unidades/{id}/cardapios` | Cardápio de cada unidade |
| Estoque | `/api/v1/unidades/{id}/estoques` | Consulta e movimentação do estoque |
| Pedidos | `/api/v1/pedidos` | Criação, consulta, atualização e cancelamento |
| Pagamentos | `/api/v1/pedidos/{id}/pagamentos` | Solicitação do pagamento mock |

Os dados de entrada, respostas, permissões e códigos HTTP de cada endpoint podem ser consultados no Swagger.

## Fluxo utilizado na demonstração

Para testar o fluxo principal, pode ser seguida esta ordem:

1. cadastrar um cliente;
2. realizar o login;
3. autenticar o administrador;
4. cadastrar um produto;
5. adicionar estoque para o produto;
6. criar um cardápio e adicionar o produto;
7. criar um pedido com os itens e o `canalPedido`;
8. solicitar o pagamento mock com uma chave de idempotência;
9. consultar o pagamento e o pedido;
10. atualizar o pedido para `EM_PREPARO`, `PRONTO` e `ENTREGUE`;
11. consultar o histórico de status.

O gateway mock permite testar pagamentos aprovados e recusados. Quando a mesma chave de idempotência é enviada novamente para a mesma operação, a API retorna o pagamento que já foi registrado.

## Coleção do Postman

A coleção está no seguinte caminho:

```text
docs/postman/raizes-do-nordeste.postman_collection.json
```

Ela foi separada nas pastas **Auth**, **Estoque e Produtos**, **Pedidos**, **Pagamentos** e **Operação de Pedidos**.

Antes de executar a coleção:

1. inicie a API;
2. confira se a variável `baseUrl` está correta;
3. configure localmente as senhas utilizadas nos logins;
4. execute as pastas na ordem em que aparecem;
5. execute o cadastro apenas uma vez para o mesmo e-mail.

Os scripts da coleção salvam os tokens e identificadores necessários para as requisições seguintes.

## Testes automatizados

Para executar todos os testes no Windows:

```powershell
.\mvnw.cmd test
```

No Linux ou macOS:

```bash
./mvnw test
```

Os testes verificam regras de negócio, serviços, persistência, segurança e tratamento de erros. Como alguns testes utilizam o banco, o PostgreSQL deve estar iniciado e as variáveis de ambiente devem estar configuradas.

## Formato dos erros

Os erros da API seguem o mesmo formato. Este é um exemplo de erro de validação:

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

Os erros `401` e `403` também utilizam esse formato.

## Segurança

As senhas são armazenadas utilizando BCrypt e não aparecem nas respostas da API. A autenticação utiliza JWT e as rotas são liberadas de acordo com o perfil do usuário.

As chaves e senhas utilizadas para executar o projeto ficam em variáveis de ambiente e não são armazenadas no repositório.

## Limitações desta versão

O fluxo completo implementado nesta versão é **Pedido → Pagamento mock → Atualização de status**, com os dados armazenados no PostgreSQL.

As funcionalidades de estorno, fidelidade, campanhas, consentimento e auditoria completa foram consideradas na modelagem, mas não foram implementadas no MVP. Elas podem ser desenvolvidas em uma versão futura do sistema.

## Repositório

[github.com/Renan-302001/raizes-do-nordeste-api](https://github.com/Renan-302001/raizes-do-nordeste-api)
