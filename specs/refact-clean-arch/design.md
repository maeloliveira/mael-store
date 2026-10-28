# Arquitetura Clean para migração do Mael-Store

## Objetivo e escopo

Este documento propõe uma arquitetura Clean para o Mael-Store e uma migração
incremental, sem exigir uma reescrita do sistema. O fluxo de compra é o caso
guia, pois `PurchaseModel` combina atualmente dados de negócio com mapeamento
JPA e é usado pelo service, mapper e eventos Spring.

O objetivo principal é inverter as dependências: regras e casos de uso não
devem conhecer Spring, JPA, HTTP ou detalhes do banco. Essas tecnologias ficam
nas bordas e se conectam ao núcleo por gateways (portas).

## Estado atual observado

- `controller` recebe HTTP e delega para `service`.
- `PurchaseMapper` converte o request, consulta clientes e livros através dos
  services e cria diretamente um `PurchaseModel`.
- `PurchaseModel` é simultaneamente entidade persistida e objeto que circula
  no fluxo. Possui `@Entity`, relacionamentos JPA, identificador de banco,
  `nfe`, preço e data de criação.
- `PurchaseService` persiste a compra por `PurchaseRepository` e publica um
  `PurchaseEvent` do Spring.
- `GenerateNfeListener` gera um UUID e atualiza a compra; `UpdateSoldBookListener`
  marca os livros como vendidos através de `BookService`.
- Services e repositórios dos demais domínios também estão organizados por
  camada técnica e usam diretamente entidades JPA.

Assim, a compra depende de infraestrutura e de services concretos fora do
controller. A arquitetura proposta separa essas responsabilidades sem mudar,
nesta primeira etapa, o contrato HTTP ou o esquema do banco.

## Princípios e dependências

1. **Domínio:** entidades e regras de negócio puras, sem anotações ou tipos de
   frameworks.
2. **Aplicação:** casos de uso que coordenam regras e definem portas/gateways
   necessários. A aplicação depende do domínio e das abstrações, nunca dos
   adapters.
3. **Adapters de entrada:** HTTP, listeners e outros mecanismos traduzem
   chamadas externas para os casos de uso.
4. **Adapters de saída:** JPA, integrações externas e publicação de mensagens
   implementam as portas definidas pelo núcleo.
5. **Composição:** Spring instancia e conecta os casos de uso e adapters na
   configuração da aplicação.

Regra de dependência:

```text
HTTP / Spring Events ──> Aplicação ──> Domínio
JPA / integrações    ──> implementam portas da Aplicação
```

O núcleo não importa classes de `org.springframework`, `jakarta.persistence`
ou `org.hibernate`.

## Organização proposta

Organizar por funcionalidade mantém as peças de cada fluxo próximas; os nomes
abaixo são uma proposta, não uma exigência de migração em uma única etapa.

```text
com.bookstore.mael.store
├── purchase
│   ├── domain
│   │   ├── Purchase.kt
│   │   └── PurchaseBook.kt
│   ├── application
│   │   ├── CreatePurchaseUseCase.kt
│   │   ├── CreatePurchaseService.kt
│   │   ├── command/
│   │   └── gateway/
│   │       ├── PurchaseRepositoryGateway.kt
│   │       ├── CustomerQueryGateway.kt
│   │       ├── BookQueryGateway.kt
│   │       ├── BookSalesGateway.kt
│   │       └── InvoiceGateway.kt
│   └── adapter
│       ├── input
│       │   ├── web/
│       │   └── event/
│       └── output
│           ├── persistence/
│           │   ├── PurchaseJpaEntity.kt
│           │   ├── SpringDataPurchaseRepository.kt
│           │   └── PurchasePersistenceAdapter.kt
│           └── invoice/
├── book
├── customer
└── configuration
```

Não é necessário criar todos esses arquivos no primeiro passo. Em particular,
os gateways de cliente e livro podem inicialmente ser implementados por
adapters que delegam aos repositories atuais.

## Modelo de domínio de compra

Separar `Purchase` da atual `PurchaseModel`. O domínio deve representar
conceitos e invariantes da compra, e não o formato das tabelas.

Uma primeira versão pode manter os mesmos dados funcionais:

```kotlin
data class Purchase(
    val id: PurchaseId? = null,
    val customerId: CustomerId,
    val bookIds: List<BookId>,
    val invoiceNumber: String? = null,
    val total: Money,
    val createdAt: Instant
)
```

Os tipos `PurchaseId`, `CustomerId`, `BookId` e `Money` são exemplos; podem
ser introduzidos gradualmente. O domínio não deve guardar `CustomerModel` ou
`BookModel`, listas JPA gerenciadas, nem anotações como `@ManyToMany`.
Relacionamentos e chaves estrangeiras são detalhes do adapter de persistência.

Regras a preservar ao extrair o domínio:

- uma compra referencia um cliente e os livros informados;
- o total corresponde à soma dos preços dos livros da compra;
- livros inexistentes devem continuar resultando no erro de domínio/API
  correspondente;
- a emissão de nota e a marcação dos livros como vendidos acontecem após a
  persistência da compra, como no fluxo atual.

A extração deve também esclarecer se livros repetidos são aceitos. Hoje
`PostPurchaseRequest.bookIds` usa `Set<Int>`, enquanto a verificação de
quantidade do mapper compara a quantidade retornada com a quantidade da
requisição; o novo fluxo deve preservar a semântica acordada e cobri-la com
testes.

## Caso de uso e gateways

O caso de uso recebe dados simples, consulta os dados necessários por portas,
calcula o total, persiste e solicita os efeitos posteriores. Ele não conhece
controller, `PurchaseRepository` do Spring Data, eventos Spring ou entidades
JPA.

Exemplo ilustrativo das portas:

```kotlin
interface CreatePurchaseUseCase {
    fun execute(command: CreatePurchaseCommand): PurchaseId
}

interface CustomerQueryGateway {
    fun exists(customerId: CustomerId): Boolean
}

interface BookQueryGateway {
    fun findByIds(bookIds: Set<BookId>): List<BookForPurchase>
}

interface PurchaseRepositoryGateway {
    fun save(purchase: Purchase): Purchase
}

interface BookSalesGateway {
    fun markAsSold(bookIds: Set<BookId>)
}

interface InvoiceGateway {
    fun issueFor(purchaseId: PurchaseId): InvoiceNumber
}
```

Os nomes e retornos são sugestões e devem acompanhar os contratos de erro já
existentes. Se a emissão de nota continuar sendo apenas a geração de um UUID,
ela pode ser uma implementação temporária de `InvoiceGateway`; o caso de uso
não deve codificar como a nota é produzida.

Fluxo sugerido para `CreatePurchaseService`:

1. Validar o cliente usando `CustomerQueryGateway`.
2. Buscar todos os livros com `BookQueryGateway`; rejeitar o pedido se algum
   identificador não for encontrado.
3. Calcular o total a partir dos preços retornados, sem confiar em preço
   recebido do cliente.
4. Criar e salvar `Purchase` por `PurchaseRepositoryGateway`.
5. Solicitar a emissão da nota e a marcação dos livros como vendidos, usando
   as portas correspondentes.

A política de sincronismo e falhas do passo 5 precisa ser explícita. Os
listeners atuais são `@Async`; portanto, a resposta da compra não garante que
ambos os efeitos posteriores já terminaram. Na migração, preservar essa
semântica inicialmente e definir como falhas assíncronas serão registradas e
reprocessadas. Caso seja necessária garantia entre gravação e publicação,
avaliar uma outbox transacional em uma etapa própria.

## Adapters e mapeamentos

### Entrada HTTP

`PurchaseController` continua responsável por receber `PostPurchaseRequest`,
traduzir o contrato HTTP para `CreatePurchaseCommand`, invocar
`CreatePurchaseUseCase` e traduzir o resultado para a resposta HTTP. Ele não
deve buscar cliente/livros nem construir entidade de persistência.

O contrato atual (`customer_id` e `book_ids`, `POST /purchase` e status
`201`) deve ser mantido durante a migração, exceto se houver uma mudança de API
planejada separadamente.

### Saída de persistência

`PurchasePersistenceAdapter` implementa `PurchaseRepositoryGateway` e usa o
Spring Data/JPA internamente. Um `PurchaseJpaEntity` conserva o mapeamento para
a tabela `purchase` e para a tabela de associação `purchase_book`. O adapter é
responsável por converter:

```text
Purchase <──> PurchaseJpaEntity
```

As chaves e colunas atuais (`customer_id`, `nfe`, `price`, `created_at`,
`purchase_id`, `book_id`) devem continuar compatíveis com o banco e com as
migrations existentes. Não é necessário renomear tabelas para introduzir
Clean Architecture.

`CustomerQueryGateway`, `BookQueryGateway` e `BookSalesGateway` também podem
ser implementados por adapters sobre `CustomerRepository` e `BookRepository`.
Evitar que o caso de uso receba `CustomerModel` ou `BookModel`: retornar apenas
os dados de leitura necessários, como identificador e preço, ou aceitar
identificadores em comandos de atualização.

### Efeitos posteriores

Os listeners Spring deixam de carregar `PurchaseModel` como evento de domínio.
Um adapter de entrada de eventos pode consumir um evento simples e chamar um
caso de uso específico, ou um adapter de saída pode publicar eventos próprios
da aplicação. Não expor `ApplicationEvent` nem objetos JPA através das portas.

Para a migração inicial, os listeners existentes podem ser mantidos como
compatibilidade, adaptando o evento legado à porta/caso de uso novo. Removê-los
somente depois de confirmar paridade funcional e tratamento de falhas.

## Plano de migração incremental

### 1. Caracterizar o comportamento

- Adicionar ou confirmar testes para criação de compra, cliente ausente,
  livros ausentes, cálculo do total, persistência e efeitos posteriores.
- Registrar os códigos e mensagens de erro, status HTTP, formato do request e
  comportamento assíncrono que precisam ser mantidos.
- Confirmar regras para duplicidade de IDs, compra sem livros e geração de NFe.

### 2. Introduzir o contrato de entrada

- Criar `CreatePurchaseCommand` e `CreatePurchaseUseCase`.
- Fazer o controller chamar a porta de entrada.
- Manter o `PurchaseService` atual como fachada temporária ou movê-lo para a
  implementação do caso de uso, sem alterar o endpoint.

### 3. Introduzir gateways ao redor do código existente

- Definir interfaces para persistência da compra, consulta de cliente/livros e
  atualização dos livros vendidos.
- Implementar adapters delegando aos repositories e services atuais.
- Manter `PurchaseModel` restrito aos adapters legados; mapear para o modelo de
  domínio antes de entrar no caso de uso.

### 4. Migrar o fluxo e os eventos

- Mover validação de existência, cálculo do total e coordenação para a
  aplicação/domínio.
- Adaptar geração de nota e marcação de livros vendidos por portas, mantendo a
  execução assíncrona atual até que a política de consistência seja definida.
- Remover a dependência do `PurchaseEvent` em `PurchaseModel`.

### 5. Consolidar a persistência e expandir por funcionalidade

- Mover entidade JPA, Spring Data e conversores para o adapter de persistência.
- Remover dependências dos casos de uso para services e modelos JPA antigos.
- Aplicar o mesmo padrão a livros e clientes em alterações pequenas, guiadas
  por casos de uso e testes.
- Excluir fachadas e adapters legados somente quando não houver mais chamadas
  para eles.

Cada etapa deve ser compilável e manter os testes existentes. Evitar uma
migração em massa de todos os controllers, services e repositories antes de
validar o fluxo de compra.

## Testes e critérios de aceite

- **Domínio:** testar invariantes e cálculo de total sem Spring ou banco.
- **Aplicação:** testar o caso de uso com gateways fake/mock, cobrindo cliente
  inválido, livros ausentes, sucesso e falhas de persistência/efeitos.
- **Adapters:** testar mapeamentos entre domínio e JPA e consultas relevantes.
- **HTTP:** testar que request, status e erros continuam compatíveis.
- **Integração:** validar constraints, relacionamentos e migrations no banco
  usado pelo projeto.
- **Arquitetura:** garantir por revisão ou teste que `domain` e `application`
  não importam Spring/JPA e que adapters dependem das portas, não o contrário.

## Decisões em aberto

- Se a geração de NFe será integração real ou permanecerá um identificador
  temporário.
- Se falhas ao marcar livros vendidos/emissão de nota exigem retry, outbox ou
  compensação.
- Se a compra deve ser atômica com a atualização do estado dos livros.
- A política de IDs repetidos, lista vazia e disponibilidade dos livros no
  momento da compra.
- Se a estrutura será organizada integralmente por funcionalidade ou se as
  outras áreas permanecerão temporariamente na estrutura atual.
