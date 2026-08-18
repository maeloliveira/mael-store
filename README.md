# Mael-Store

O Mael-Store é um serviço de gerenciamento de livraria, desenvolvido em Kotlin e Spring Boot.

## Estrutura do Projeto

O projeto segue uma estrutura padrão para aplicações Spring Boot:

```
.
├── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── src
    ├── main
    │   ├── kotlin
    │   │   └── com
    │   │       └── bookstore
    │   │           └── mael
    │   │               └── store
    │   │                   ├── MaelStoreApplication.kt
    │   │                   ├── config
    │   │                   ├── controller
    │   │                   ├── enums
    │   │                   ├── events
    │   │                   ├── exception
    │   │                   ├── extension
    │   │                   ├── model
    │   │                   ├── repository
    │   │                   ├── request
    │   │                   ├── service
    │   │                   └── validation
    │   └── resources
    │       ├── application.properties
    │       └── static
    └── test
        └── kotlin
```

- **`build.gradle.kts`**: Arquivo de build do Gradle, onde são definidas as dependências e configurações do projeto.
- **`src/main/kotlin`**: Contém o código-fonte da aplicação.
- **`src/main/resources`**: Contém os arquivos de configuração, como o `application.properties`.
- **`src/test/kotlin`**: Contém os testes da aplicação.

## Como Subir na sua Máquina

Para executar o projeto em sua máquina, siga os passos abaixo:

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/mael-store.git
   ```

2. **Navegue até o diretório do projeto:**
   ```bash
   cd mael-store
   ```

3. **Execute a aplicação:**
   ```bash
   ./gradlew bootRun
   ```

A aplicação estará disponível em `http://localhost:8080`.

## Descrição do Serviço

O Mael-Store é um sistema de gerenciamento de livraria que oferece as seguintes funcionalidades:

- **Gerenciamento de Livros:** Cadastro, atualização, exclusão e consulta de livros.
- **Gerenciamento de Clientes:** Cadastro, atualização, exclusão e consulta de clientes.
- **Gerenciamento de Vendas:** Registro de vendas de livros para clientes.

O serviço é construído utilizando as seguintes tecnologias:

- **Kotlin:** Linguagem de programação moderna e concisa.
- **Spring Boot:** Framework para criação de aplicações Java/Kotlin de forma rápida e fácil.
- **Gradle:** Ferramenta de automação de build.
- **H2 (em memória):** Banco de dados para desenvolvimento e testes.
