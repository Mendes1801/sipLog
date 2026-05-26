# ⚙️ core_api — Microsserviço Principal do SipLog

Este diretório contém o **microsserviço principal** do SipLog: toda a lógica de negócio, regras de domínio e persistência de dados da plataforma. É o coração do sistema.

---

## Sumário

1. [Responsabilidades](#responsabilidades)
2. [Estrutura de Pastas](#estrutura-de-pastas)
3. [Camadas da Aplicação](#camadas-da-aplicação)
4. [Endpoints Internos](#endpoints-internos)
5. [Entidades e Relacionamentos](#entidades-e-relacionamentos)
6. [Segurança](#segurança)
7. [Variáveis de Ambiente](#variáveis-de-ambiente)
8. [Como Executar](#como-executar)
9. [Build e Docker](#build-e-docker)
10. [Testes](#testes)

---

## Responsabilidades

A Core API é uma **API REST interna**: ela **não é exposta diretamente ao aplicativo mobile**. Toda comunicação com o frontend passa pelo `sipLog-BFF`, que valida a autenticação e repassa as requisições.

Responsabilidades desta camada:

- **CRUD completo** das entidades: Usuários, Bebidas, Experiências, Curtidas, Comentários, Notificações e Seguidores
- **Persistência** no banco de dados PostgreSQL via JPA/Hibernate
- **Validação de regras de negócio** (acesso negado, recurso não encontrado, etc.)
- **Emissão de eventos de domínio** — ex.: ao receber uma curtida ou comentário, dispara um `NotificacaoEvent` para gerar a notificação correspondente
- **Cálculo de campos derivados** via `@Formula` do Hibernate (total de curtidas, total de comentários)

---

## Estrutura de Pastas

```
core_api/
└── apiCore-sipLog/
    ├── src/
    │   ├── main/
    │   │   ├── java/br/mackenzie/labEngenhariaSW/sipLog/apiCore_sipLog/
    │   │   │   ├── ApiCoreSipLogApplication.java   ← Ponto de entrada Spring Boot
    │   │   │   ├── config/
    │   │   │   │   ├── JwtConfig.java              ← Extração do subject JWT
    │   │   │   │   └── SecurityConfig.java         ← Configuração OAuth2 Resource Server
    │   │   │   ├── controller/
    │   │   │   │   ├── BebidaCoreController.java
    │   │   │   │   ├── ExperienciaCoreController.java
    │   │   │   │   ├── FeedCoreController.java
    │   │   │   │   ├── NotificacaoCoreController.java
    │   │   │   │   ├── UsuariosCoreController.java
    │   │   │   │   └── GlobalExceptionHandler.java
    │   │   │   ├── service/
    │   │   │   │   ├── BebidaCoreService.java
    │   │   │   │   ├── ExperienciaCoreService.java
    │   │   │   │   ├── FeedCoreService.java
    │   │   │   │   ├── NotificacaoCoreService.java
    │   │   │   │   └── UsuarioCoreService.java
    │   │   │   ├── repository/
    │   │   │   │   ├── BebidaRepository.java
    │   │   │   │   ├── ComentarioRepository.java
    │   │   │   │   ├── CurtidaRepository.java
    │   │   │   │   ├── ExperienciaRepository.java
    │   │   │   │   ├── NotificacaoRepository.java
    │   │   │   │   ├── SeguidorRepository.java
    │   │   │   │   └── UsuarioRepository.java
    │   │   │   ├── entity/
    │   │   │   │   ├── Bebida.java
    │   │   │   │   ├── Comentario.java
    │   │   │   │   ├── Curtida.java
    │   │   │   │   ├── Experiencia.java           ← Contém enum Visibilidade
    │   │   │   │   ├── Notificacao.java
    │   │   │   │   ├── Seguidor.java
    │   │   │   │   ├── TipoNotificacao.java       ← Enum: CURTIDA | COMENTARIO | NOVO_SEGUIDOR
    │   │   │   │   └── Usuario.java
    │   │   │   ├── dto/
    │   │   │   │   ├── dtoGet/                    ← Objetos de resposta (saída)
    │   │   │   │   │   ├── BebidaDetalheDTO.java
    │   │   │   │   │   ├── BebidaResumoDTO.java
    │   │   │   │   │   ├── ComentarioDTO.java
    │   │   │   │   │   ├── ComentarioResponseDTO.java
    │   │   │   │   │   ├── ContagemNotificacoesDTO.java
    │   │   │   │   │   ├── FeedItemDTO.java
    │   │   │   │   │   ├── NotificacaoResponseDTO.java
    │   │   │   │   │   ├── PerfilDTO.java
    │   │   │   │   │   ├── RegistroExperienciaDTO.java
    │   │   │   │   │   └── UsuarioResumoDTO.java
    │   │   │   │   ├── dtoPost/                   ← Objetos de entrada (criação)
    │   │   │   │   │   ├── NovaBebidaDTO.java
    │   │   │   │   │   ├── NovaExperienciaDTO.java
    │   │   │   │   │   ├── NovoComentarioDTO.java
    │   │   │   │   │   ├── SeguirAmigoDTO.java
    │   │   │   │   │   └── UsuarioSyncDTO.java
    │   │   │   │   └── dtoPut/                    ← Objetos de entrada (atualização)
    │   │   │   │       └── UsuarioUpdateDTO.java
    │   │   │   ├── event/
    │   │   │   │   └── NotificacaoEvent.java      ← Evento Spring para geração de notificações
    │   │   │   └── exception/
    │   │   │       ├── AcessoNegadoException.java
    │   │   │       └── RecursoNaoEncontradoException.java
    │   │   └── resources/
    │   │       └── application.yaml
    │   └── test/
    │       └── …/ApiCoreSipLogApplicationTests.java
    ├── Dockerfile
    ├── Dockerfile.core-local
    ├── mvnw / mvnw.cmd
    └── pom.xml
```

---

## Camadas da Aplicação

A aplicação segue o padrão **MVC em camadas**:

| Camada | Pacote | Responsabilidade |
|---|---|---|
| **Controller** | `controller/` | Receber requisições HTTP, validar entrada, delegar ao Service |
| **Service** | `service/` | Regras de negócio, orquestração, disparo de eventos |
| **Repository** | `repository/` | Interfaces Spring Data JPA; queries customizadas com JPQL |
| **Entity** | `entity/` | Mapeamento ORM das tabelas do banco (JPA + Hibernate) |
| **DTO** | `dto/` | Contratos de transferência (evita expor entidades diretamente) |
| **Event** | `event/` | Eventos Spring (`ApplicationEvent`) para comunicação entre serviços |
| **Exception** | `exception/` | Exceções de domínio customizadas (lançadas pelos services) |
| **Config** | `config/` | Beans de segurança e configuração JWT |

### Tratamento de erros

O `GlobalExceptionHandler` captura exceções de domínio e retorna respostas HTTP padronizadas:

| Exceção | HTTP Status |
|---|---|
| `RecursoNaoEncontradoException` | `404 Not Found` |
| `AcessoNegadoException` | `403 Forbidden` |
| Outras | `500 Internal Server Error` |

---

## Endpoints Internos

> Estes endpoints são consumidos **exclusivamente pelo BFF**. Base path: `/apiCore/v1/`

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/apiCore/v1/usuarios/sync` | Cria ou atualiza usuário com dados do JWT |
| `GET` | `/apiCore/v1/usuarios/{keycloakId}` | Busca perfil por ID do Keycloak |
| `PUT` | `/apiCore/v1/usuarios/{keycloakId}` | Atualiza dados do perfil |
| `DELETE` | `/apiCore/v1/usuarios/{keycloakId}` | Remove conta |
| `GET` | `/apiCore/v1/usuarios/buscar` | Busca usuários por nome |
| `GET` | `/apiCore/v1/usuarios/{id}/seguidores` | Lista seguidores |
| `GET` | `/apiCore/v1/usuarios/{id}/seguindo` | Lista usuários seguidos |
| `POST` | `/apiCore/v1/usuarios/{id}/seguir` | Seguir usuário |
| `DELETE` | `/apiCore/v1/usuarios/{id}/seguir` | Deixar de seguir |

### Experiências

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/apiCore/v1/experiencias` | Cria nova experiência |
| `PUT` | `/apiCore/v1/experiencias/{id}` | Edita experiência |
| `DELETE` | `/apiCore/v1/experiencias/{id}` | Remove experiência |
| `POST` | `/apiCore/v1/experiencias/{id}/curtir` | Curtir/descurtir |
| `GET` | `/apiCore/v1/experiencias/{id}/comentarios` | Lista comentários |
| `POST` | `/apiCore/v1/experiencias/{id}/comentarios` | Adiciona comentário |
| `PUT` | `/apiCore/v1/experiencias/{id}/comentarios/{idComentario}` | Edita comentário |
| `DELETE` | `/apiCore/v1/experiencias/{id}/comentarios/{idComentario}` | Remove comentário |

### Feed

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/apiCore/v1/feed/me?pagina=0` | Feed global paginado |
| `GET` | `/apiCore/v1/feed/amigos?pagina=0` | Feed de amigos |
| `GET` | `/apiCore/v1/feed/usuarios/{id}?pagina=0` | Feed de um usuário específico |

### Bebidas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/apiCore/v1/bebidas/buscar?nome=...` | Busca com autocomplete |
| `GET` | `/apiCore/v1/bebidas/{id}` | Detalhes de uma bebida |
| `POST` | `/apiCore/v1/bebidas` | Cadastra nova bebida |

### Notificações

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/apiCore/v1/notificacoes/{keycloakId}` | Lista notificações do usuário |
| `GET` | `/apiCore/v1/notificacoes/{keycloakId}/nao-lidas/count` | Contagem de não lidas |
| `PATCH` | `/apiCore/v1/notificacoes/{id}/lida` | Marca como lida |

---

## Entidades e Relacionamentos

### `Usuario`
Representa um usuário cadastrado na plataforma. O campo `keycloakId` (UUID) vincula o registro ao Identity Provider sem acoplamento.

### `Bebida`
Catálogo de bebidas. O campo `caracteristicas` usa o tipo **JSONB** do PostgreSQL para armazenar atributos dinâmicos por categoria:
```json
// Cerveja
{ "ibu": "40", "estilo": "IPA", "teor_alcoolico": "6.5" }

// Vinho
{ "safra": "2019", "uva": "Malbec", "regiao": "Mendoza" }
```

### `Experiencia`
O objeto central da plataforma. Um usuário registra a experiência com uma bebida específica, atribuindo nota, comentário, foto e localização. Possui o enum `Visibilidade` (`PUBLICA`, `AMIGOS`, `PRIVADA`).

### `Seguidor`
Tabela de relacionamento N:N que representa o grafo de conexões entre usuários.

### `Notificacao`
Gerada automaticamente via `NotificacaoEvent` quando ocorrem curtidas, comentários ou novos seguidores.

---

## Segurança

A Core API também atua como **OAuth2 Resource Server**: valida JWTs do Keycloak em todas as requisições. A configuração está em `SecurityConfig.java` e `JwtConfig.java`.

```yaml
# application.yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_SERVER_URL}
```

> Mesmo sendo uma API interna, toda requisição deve carregar o Bearer Token repassado pelo BFF via `TokenRelayInterceptor`.

---

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `KEYCLOAK_SERVER_URL` | URL completa do realm Keycloak | Obrigatório |
| `DB_NAME` | Nome do banco de dados PostgreSQL | Obrigatório |
| `DB_USER` | Usuário do PostgreSQL | Obrigatório |
| `DB_PASSWORD` | Senha do PostgreSQL | Obrigatório |

O `application.yaml` usa a sintaxe `${VARIAVEL}` do Spring para injeção.

---

## Como Executar

### Pré-condição

O PostgreSQL e o Keycloak devem estar rodando. Use o Docker Compose da pasta `infra/`:

```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d postgres-local postgres-keycloak keycloak
```

### Executar em modo desenvolvimento

```bash
cd core_api/apiCore-sipLog

# Exportar variáveis (se não usar docker-compose)
export KEYCLOAK_SERVER_URL=http://localhost:8080/realms/BFF
export DB_NAME=sip_db
export DB_USER=sip_user
export DB_PASSWORD=senha_local

./mvnw spring-boot:run
```

A API iniciará na **porta 8082**. O Hibernate criará as tabelas automaticamente.

---

## Build e Docker

### Gerar o JAR

```bash
cd core_api/apiCore-sipLog
./mvnw clean package -DskipTests
```

O JAR gerado fica em `target/apiCore-sipLog-0.0.1-SNAPSHOT.jar`.

### Build da imagem Docker

Há dois Dockerfiles:

| Arquivo | Uso |
|---|---|
| `Dockerfile` | Build de produção (multi-stage) |
| `Dockerfile.core-local` | Build otimizado para desenvolvimento local |

```bash
# Build da imagem de produção
docker build -t siplog-core-api:latest -f Dockerfile .
```

---

## Testes

Os testes usam banco **H2 em memória** (configurado em `src/test/resources/application.yaml`), eliminando dependências externas:

```bash
cd core_api/apiCore-sipLog
./mvnw test
```

Os relatórios ficam em `target/surefire-reports/`.

---

> **Dependências de runtime:** PostgreSQL 15 · Keycloak 24.0.0
> **Porta:** `8082`
> **Consumido por:** `sipLog-BFF` exclusivamente
