# Plano de Testes da API Raízes do Nordeste

## 1. Objetivo

Este plano apresenta os cenários utilizados para validar o fluxo principal da API Raízes do Nordeste. Os testes verificam autenticação, autorização, validação de dados, estoque por unidade, criação de pedidos, pagamento mock, atualização de status e rastreabilidade das alterações.

Os cenários estão implementados na coleção `postman/raizes-do-nordeste.postman_collection.json`. A coleção deve ser executada com a API e o PostgreSQL em funcionamento.

## 2. Estratégia

Foram utilizados testes manuais e scripts de asserção no Postman. Cada requisição relevante verifica o código HTTP e os campos principais da resposta. A coleção também armazena tokens e identificadores necessários para as etapas seguintes do fluxo.

Os testes estão divididos em:

- cenários positivos, nos quais a operação deve ser concluída;
- cenários negativos, nos quais a API deve rejeitar a operação de forma controlada;
- verificações de segurança, diferenciando falta de autenticação e falta de permissão;
- verificação de rastreabilidade por meio do histórico de status do pedido.

## 3. Pré-condições gerais

- PostgreSQL iniciado e banco `raizes_nordeste` disponível;
- migrations executadas pelo Flyway;
- API iniciada em `http://localhost:8080`;
- administrador de desenvolvimento cadastrado;
- cliente de teste cadastrado;
- variáveis `adminPassword` e `clientPassword` definidas apenas no ambiente local do Postman;
- requisições executadas na ordem das pastas da coleção quando houver dependência de dados.

## 4. Cenários positivos

### T01 — Autenticar administrador

- **Endpoint:** `POST /api/v1/auth/login`
- **Pré-condição:** administrador ativo cadastrado pelo seed de desenvolvimento.
- **Entrada:** e-mail do administrador e senha local válida.
- **Saída esperada:** HTTP `200`, `accessToken` preenchido, `tokenType` igual a `Bearer` e tempo de expiração informado.
- **Evidência na coleção:** `Estoque e Produtos / Login - Administrador`.

### T02 — Listar unidades publicamente

- **Endpoint:** `GET /api/v1/unidades?page=0&size=20`
- **Pré-condição:** ao menos uma unidade cadastrada.
- **Entrada:** parâmetros de paginação.
- **Saída esperada:** HTTP `200` e resposta paginada contendo a unidade de demonstração.
- **Evidência na coleção:** `Estoque e Produtos / Listar Unidade - público`.

### T03 — Cadastrar produto como administrador

- **Endpoint:** `POST /api/v1/produtos`
- **Pré-condição:** administrador autenticado e código público ainda não utilizado.
- **Entrada:** código público, nome, descrição e categoria do produto.
- **Saída esperada:** HTTP `201`, identificador gerado e status do produto igual a `ATIVO`.
- **Evidência na coleção:** `Estoque e Produtos / Cadastrar produto - administrador`.

### T04 — Registrar entrada de estoque

- **Endpoint:** `POST /api/v1/unidades/{idUnidade}/estoques/{idProduto}/movimentacoes`
- **Pré-condição:** administrador autenticado, unidade e produto existentes.
- **Entrada:** tipo `ENTRADA`, quantidade positiva e motivo da movimentação.
- **Saída esperada:** HTTP `201`, movimentação registrada e saldo disponível aumentado.
- **Evidência na coleção:** `Estoque e Produtos / Registrar entrada de estoque`.

### T05 — Criar pedido com itens válidos

- **Endpoint:** `POST /api/v1/pedidos`
- **Pré-condição:** unidade ativa, produto disponível no cardápio e saldo suficiente no estoque.
- **Entrada:** unidade, `canalPedido` igual a `TOTEM` e item com quantidade positiva.
- **Saída esperada:** HTTP `201`, identificador gerado, status `AGUARDANDO_PAGAMENTO` e reserva do estoque.
- **Evidência na coleção:** `Pedidos / Criar pedido - sucesso`.

### T06 — Aprovar pagamento mock

- **Endpoint:** `POST /api/v1/pedidos/{idPedido}/pagamentos`
- **Pré-condição:** cliente autenticado e pedido aguardando pagamento.
- **Entrada:** header `Idempotency-Key` exclusivo e resultado simulado `APROVADO`.
- **Saída esperada:** HTTP `201`, pagamento `APROVADO` e pedido `CONFIRMADO`.
- **Evidência na coleção:** `Pagamentos / Pagamento aprovado`.

### T07 — Registrar recusa do pagamento mock

- **Endpoint:** `POST /api/v1/pedidos/{idPedido}/pagamentos`
- **Pré-condição:** cliente autenticado e novo pedido aguardando pagamento.
- **Entrada:** header `Idempotency-Key` exclusivo e resultado simulado `RECUSADO`.
- **Saída esperada:** HTTP `201`, pagamento `RECUSADO` e pedido `PAGAMENTO_RECUSADO`, sem confirmar a venda.
- **Evidência na coleção:** `Pagamentos / Pagamento recusado`.

### T08 — Registrar entrega do pedido

- **Endpoint:** `PATCH /api/v1/pedidos/{idPedido}/status`
- **Pré-condição:** pedido pago que já passou pelos estados `EM_PREPARO` e `PRONTO`; usuário autorizado.
- **Entrada:** novo status `ENTREGUE` e observação operacional.
- **Saída esperada:** HTTP `200`, status do pedido igual a `ENTREGUE` e data da entrega registrada.
- **Evidência na coleção:** `Operação de Pedidos / Registrar entrega do pedido`.

### T09 — Consultar histórico de status

- **Endpoint:** `GET /api/v1/pedidos/{idPedido}/historico-status`
- **Pré-condição:** pedido existente com alterações de status registradas.
- **Entrada:** identificador do pedido.
- **Saída esperada:** HTTP `200` e lista cronológica contendo as mudanças realizadas.
- **Evidência na coleção:** `Operação de Pedidos / Consultar histórico de status`.

## 5. Cenários negativos

### T10 — Acessar listagem protegida sem token

- **Endpoint:** `GET /api/v1/pedidos`
- **Pré-condição:** requisição configurada sem autenticação.
- **Entrada:** nenhuma credencial.
- **Saída esperada:** HTTP `401` e código de erro `UNAUTHENTICATED`.
- **Evidência na coleção:** `Pedidos / Listar pedidos sem token - 401`.

### T11 — Cadastrar produto com perfil sem permissão

- **Endpoint:** `POST /api/v1/produtos`
- **Pré-condição:** cliente autenticado com token válido.
- **Entrada:** dados válidos de produto e token do cliente.
- **Saída esperada:** HTTP `403` e código de erro `ACCESS_DENIED`.
- **Evidência na coleção:** `Estoque e Produtos / Cadastrar produto - cliente sem permissão`.

### T12 — Criar pedido com produto inexistente

- **Endpoint:** `POST /api/v1/pedidos`
- **Pré-condição:** unidade existente e ativa.
- **Entrada:** identificador de produto inexistente, canal `APP` e quantidade igual a `1`.
- **Saída esperada:** HTTP `404` e código de erro `RESOURCE_NOT_FOUND`.
- **Evidência na coleção:** `Pedidos / Criar pedido - produto inexistente - 404`.

### T13 — Criar pedido sem informar o canal

- **Endpoint:** `POST /api/v1/pedidos`
- **Pré-condição:** unidade e produto válidos.
- **Entrada:** corpo sem o campo obrigatório `canalPedido`.
- **Saída esperada:** HTTP `422`, código `VALIDATION_ERROR` e detalhe associado a `canalPedido`.
- **Evidência na coleção:** `Pedidos / Criar pedido - canal obrigatório`.

### T14 — Criar pedido com estoque insuficiente

- **Endpoint:** `POST /api/v1/pedidos`
- **Pré-condição:** unidade, produto, cardápio e estoque existentes.
- **Entrada:** quantidade solicitada superior ao saldo disponível.
- **Saída esperada:** HTTP `409` e código de erro `BUSINESS_RULE_VIOLATION`.
- **Evidência na coleção:** `Pedidos / Criar pedido - estoque insuficiente`.

## 6. Critério de aprovação

O fluxo é considerado aprovado quando:

- os cenários positivos retornam os códigos HTTP e estados de domínio esperados;
- os cenários negativos são rejeitados sem alteração indevida dos dados;
- todas as falhas usam o formato padronizado de erro;
- pagamento aprovado e recusado produzem resultados coerentes no pedido;
- as transições do pedido permanecem disponíveis no histórico;
- os scripts correspondentes aparecem aprovados no Postman.

