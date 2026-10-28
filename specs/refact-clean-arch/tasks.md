# Tarefas de migração para Clean Architecture

Este checklist detalha a migração incremental definida em
[design.md](design.md). Executar cada etapa em mudanças pequenas e compiláveis,
mantendo o contrato HTTP e o esquema do banco existentes.

## 1. Caracterizar o comportamento atual

- [x] Mapear os fluxos existentes de compra, livro e cliente, incluindo
      endpoints, services, repositories, listeners e códigos de erro.
- [x] Criar testes MVC do endpoint `POST /purchase`, cobrindo resposta `201`,
      aliases (`customer_id`, `book_ids`) e resposta `404` para erro de domínio.
- [x] Cobrir cálculo do total, cliente/livro inexistente, conjunto vazio e a
      ordem de persistência/publicação do evento.
- [x] Registrar e testar os efeitos dos listeners, suas anotações assíncronas
      e o uso do mesmo modelo de compra.
- [ ] Validar a gravação JPA real da associação `purchase_book` em banco de
      teste isolado.
- [ ] Definir com o negócio as regras de compra sem livros, disponibilidade
      dos livros e falhas nos efeitos posteriores. IDs duplicados são
      eliminados atualmente por `Set<Int>`.

**Pronto quando:** os testes e as decisões registram a semântica a preservar
antes de alterar o fluxo. Evidências e pendências estão em
[current-behavior.md](current-behavior.md); faltam banco de teste e decisões de
negócio para concluir esta etapa.

## 2. Criar o núcleo da funcionalidade de compra

- [ ] Criar o pacote de compra com `domain` e `application`, sem dependências
      de Spring ou JPA.
- [ ] Definir `Purchase` como modelo de domínio independente de
      `PurchaseModel`, `CustomerModel` e `BookModel`.
- [ ] Definir `CreatePurchaseCommand` com os identificadores necessários para
      iniciar uma compra.
- [ ] Definir `CreatePurchaseUseCase` como porta de entrada da aplicação.
- [ ] Definir gateways de saída para persistência da compra, consulta do
      cliente, consulta dos livros e atualização dos livros vendidos.
- [ ] Definir a porta de emissão de nota somente após esclarecer se o
      comportamento atual (UUID) será mantido ou substituído por integração.
- [ ] Implementar `CreatePurchaseService` para validar cliente e livros,
      calcular o total e coordenar a persistência pelos gateways.
- [ ] Adicionar testes unitários do caso de uso usando gateways fake ou mocks:
      sucesso, cliente inexistente, livros faltantes, total e falha de
      persistência.

**Pronto quando:** o caso de uso funciona sem Spring, JPA, banco ou objetos de
transporte HTTP.

## 3. Conectar HTTP ao caso de uso

- [ ] Adaptar `PurchaseController` para converter `PostPurchaseRequest` em
      `CreatePurchaseCommand` e invocar `CreatePurchaseUseCase`.
- [ ] Remover do `PurchaseMapper` a busca direta de cliente/livros e a criação
      de `PurchaseModel`; manter apenas mapeamentos de transporte necessários.
- [ ] Preservar rota, aliases JSON, status HTTP e formato das respostas/erros.
- [ ] Executar testes do controller e de integração para confirmar compatibilidade.

**Pronto quando:** a borda HTTP não consulta services/repositories nem conhece
entidades JPA.

## 4. Implementar gateways sobre a infraestrutura atual

- [ ] Criar adapter de saída para `PurchaseRepositoryGateway`, inicialmente
      delegando ao `PurchaseRepository` existente.
- [ ] Criar adapters para consulta de cliente e livros, usando os repositories
      existentes e retornando somente dados necessários ao caso de uso.
- [ ] Criar adapter para marcar livros como vendidos através do mecanismo atual.
- [ ] Criar mapeamentos explícitos entre o modelo de domínio e
      `PurchaseModel`; manter `PurchaseModel` dentro da infraestrutura legada.
- [ ] Garantir que o adapter preserve tabelas, colunas, relacionamentos e
      migrations existentes.
- [ ] Adicionar testes dos adapters e dos mapeamentos, sem mover ainda as
      entidades de livro e cliente.

**Pronto quando:** a aplicação depende apenas dos gateways; os adapters
encapsulam o acesso ao código atual.

## 5. Migrar os efeitos posteriores da compra

- [ ] Definir a política de sincronismo para emissão de nota e atualização dos
      livros, preservando inicialmente a execução assíncrona atual.
- [ ] Substituir a dependência dos listeners em `PurchaseModel` por dados
      simples, como `PurchaseId` e IDs dos livros.
- [ ] Adaptar os listeners Spring para chamar casos de uso/portas, sem colocar
      `ApplicationEvent` no domínio ou na aplicação.
- [ ] Definir como falhas assíncronas serão registradas e reprocessadas.
- [ ] Avaliar outbox transacional somente se for requisito garantir que eventos
      não se percam entre a gravação da compra e a publicação.
- [ ] Testar emissão de nota e atualização dos livros, incluindo cenários de
      falha e repetição conforme a política definida.

**Pronto quando:** listeners não transportam entidades JPA e os efeitos
posteriores têm política de falha documentada e testada.

## 6. Remover dependências legadas do fluxo de compra

- [ ] Remover chamadas ao `PurchaseService` antigo e ao `PurchaseMapper`
      legado quando não houver mais consumidores.
- [ ] Mover a entidade JPA, repository Spring Data e conversores para o adapter
      de persistência, sem renomear tabelas ou alterar o schema nesta etapa.
- [ ] Verificar que `domain` e `application` não importam Spring, JPA ou
      Hibernate.
- [ ] Remover classes/eventos/fachadas antigas somente após confirmar que não
      há referências restantes.
- [ ] Executar testes unitários, integração, build e inspeção de dependências.

**Pronto quando:** o fluxo de compra está migrado e a infraestrutura só é
acessada através dos adapters e gateways.

## 7. Expandir a migração para livros e clientes

- [ ] Inventariar os casos de uso de livro e cliente e priorizar um fluxo por
      vez.
- [ ] Criar portas de entrada, modelos de domínio e gateways para o primeiro
      fluxo priorizado.
- [ ] Adaptar controllers e persistência desse fluxo sem mudar contratos
      públicos ou schema sem decisão explícita.
- [ ] Repetir a migração por funcionalidade, adicionando testes de domínio,
      aplicação, adapters e HTTP.
- [ ] Remover services e modelos legados quando não houver mais dependências.

**Pronto quando:** cada funcionalidade migrada respeita a regra de dependência
e as funcionalidades restantes estão identificadas como dívida técnica.

## Critérios finais de aceite

- [ ] Testes existentes e novos passam.
- [ ] Os contratos HTTP e o schema do banco permanecem compatíveis, salvo
      alterações aprovadas separadamente.
- [ ] Casos de uso e domínio são testáveis sem subir Spring ou banco.
- [ ] Adapters implementam os gateways definidos pelo núcleo; dependências não
      apontam da aplicação para infraestrutura.
- [ ] O comportamento assíncrono, tratamento de falhas e decisões de negócio
      relevantes estão cobertos por testes e documentados.
