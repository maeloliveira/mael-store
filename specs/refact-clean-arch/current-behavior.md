# Baseline do comportamento atual

Inventário levantado antes da migração para Clean Architecture. Os testes de
caracterização correspondentes estão em `src/test/kotlin/.../Purchase*Test.kt`
e `src/test/kotlin/.../PurchaseListenersTest.kt`.

## Fluxo de compra observado

1. `POST /purchase` recebe `PostPurchaseRequest` e responde `201 Created` sem
   corpo.
2. O request usa `customer_id` e `book_ids` como aliases JSON. `bookIds` é um
   `Set<Int>`, portanto IDs repetidos são eliminados na desserialização.
3. `PurchaseMapper` busca cliente por `CustomerService.findById`, busca os
   livros por `BookService.findAllByIds`, falha se a quantidade retornada for
   diferente da solicitada e calcula o total pela soma dos preços dos livros.
4. Cliente inexistente produz `NotFoundException` com código `ML-202`, mapeado
   por `ControllerAdvice` para HTTP `404`. Livro faltante produz
   `NotFoundException` com código `ML-101`, também HTTP `404`. A mensagem de
   livro contém a representação da coleção de IDs solicitados.
5. `PurchaseService.create` salva `PurchaseModel` e depois publica
   `PurchaseEvent` contendo a mesma instância do modelo.
6. `GenerateNfeListener` gera um UUID, altera `purchase.nfe` e chama
   `PurchaseService.update`. `UpdateSoldBookListener` chama
   `BookService.purchase` com os livros do evento. Ambos usam `@Async` e
   `@EventListener`; `@EnableAsync` está na aplicação.

## Persistência e efeitos

- `PurchaseModel` é entidade JPA com tabela `purchase`, FK `customer_id`,
  colunas `nfe`, `price` e `created_at`; a relação de livros usa
  `purchase_book(purchase_id, book_id)`.
- O repository é `CrudRepository<PurchaseModel, Int>`.
- O estado inicial dos livros não é verificado pelo mapper antes da compra.
  A atualização para `VENDIDO` acontece no listener assíncrono.
- Os dois listeners recebem a mesma instância de `PurchaseModel`. Não há
  garantia de ordenação entre eles; falhas dos listeners não são retornadas
  pela chamada HTTP e não há política explícita de retry/outbox observada.
- O teste atual de aplicação desabilita Flyway e a configuração principal
  aponta para PostgreSQL local. Não há banco embarcado configurado nos testes;
  por isso os testes adicionados caracterizam o limite HTTP e as colaborações
  em memória, mas ainda não validam a gravação JPA real de `purchase_book`.

## Validações e comportamento ainda indefinido

- Embora `PostPurchaseRequest` declare constraints Jakarta, o método do
  controller não usa `@Valid`; portanto não há validação de bean explicitamente
  acionada no endpoint atual.
- O mapper aceita conjunto vazio: a compra é criada com total zero.
- IDs duplicados no JSON são colapsados pelo tipo `Set`.
- Não há verificação explícita de cliente ativo ou de disponibilidade/estado
  dos livros antes da criação da compra.
- A configuração habilita processamento assíncrono, mas os testes de
  caracterização invocam os listeners diretamente e verificam suas anotações
  e efeitos; não medem scheduling, timing ou comportamento em falhas de thread.
- Permanecem decisões de negócio: aceitar compra vazia, regra de elegibilidade
  dos livros, concorrência de venda, retry/compensação em falhas assíncronas e
  requisito de consistência entre compra, nota e venda dos livros.
- A validação real do schema e da migration `purchase_book` depende de definir
  uma configuração de banco de teste isolada.

## Endpoints e serviços relacionados

- Compras: `POST /purchase`.
- Livros: `POST /books`, `GET /books`, `GET /books/canceled`,
  `GET /books/{id}`, `GET /books/active`, `DELETE /books/{id}` e
  `PUT /books/{id}`.
- Clientes: `GET /customer`, `POST /customer`, `GET /customer/{id}`,
  `PUT /customer/{id}` e `DELETE /customer/{id}`.
- `CustomerService.deleteCustomer` desativa o cliente e marca os livros
  associados como `DELETADO`; a atualização de livro é persistida em lote.
