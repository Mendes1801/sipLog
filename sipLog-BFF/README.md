# 🛡️ sipLog-BFF — Backend for Frontend

O **BFF (Backend for Frontend)** é a **única porta de entrada** do aplicativo mobile para o ecossistema SipLog. Ele abstrai a complexidade do backend, agrega chamadas, valida segurança e gerencia integrações externas (AWS S3).

---

## Sumário

1. [Responsabilidades](#responsabilidades)
2. [Estrutura de Pastas](#estrutura-de-pastas)
3. [Endpoints Expostos ao Mobile](#endpoints-expostos-ao-mobile)
4. [Fluxo de uma Requisição](#fluxo-de-uma-requisição)
5. [Segurança — JWT e Keycloak](#segurança--jwt-e-keycloak)
6. [Integração AWS S3](#integração-aws-s3)
7. [Variáveis de Ambiente](#variáveis-de-ambiente)
8. [Como Executar](#como-executar)
9. [Build e Docker](#build-e-docker)
10. [Swagger UI](#swagger-ui)

---

## Responsabilidades

| Responsabilidade | Detalhe |
|---|---|
| **Gateway** | Único ponto de entrada do app mobile; encaminha requisições para a Core API |
| **Autenticação** | Atua como OAuth2 Resource Server; valida Bearer Tokens emitidos pelo Keycloak |
| **Token Relay** | Repassa a identidade do usuário (JWT) para a Core API via `TokenRelayInterceptor` |
| **Upload de Mídia** | Recebe arquivos multipart do Flutter e os envia ao AWS S3 (`StorageBffService`) |
| **Agregação de Dados** | Combina múltiplas chamadas à Core API em uma única resposta formatada para o Flutter |
| **Formatação** | Converte timestamps para texto legível ("Há 2h"), calcula campos derivados, padroniza enums |
| **Sincronização de Usuário** | No primeiro login, extrai dados do JWT e persiste o usuário na Core API (`/sync`) |

---

## Estrutura de Pastas

```
sipLog-BFF/
├── src/
│   ├── main/
│   │   ├── java/br/mackenzie/labEngenhariaSW/sipLogBFF/
│   │   │   ├── SipLogBFFApplication.java          ← Ponto de entrada Spring Boot
│   │   │   ├── config/
│   │   │   │   ├── RestClientConfig.java          ← Configuração do RestClient para chamar a Core API
│   │   │   │   ├── S3Config.java                  ← Bean do cliente AWS S3
│   │   │   │   ├── SecurityConfig.java            ← OAuth2 Resource Server + CSRF/CORS
│   │   │   │   └── TokenRelayInterceptor.java     ← Injeta o JWT nas chamadas à Core API
│   │   │   ├── controller/
│   │   │   │   ├── BebidaBffController.java
│   │   │   │   ├── ExperienciaBffController.java
│   │   │   │   ├── FeedBffController.java
│   │   │   │   ├── NotificacaoBffController.java
│   │   │   │   ├── ReportBffController.java
│   │   │   │   ├── UploadBffController.java
│   │   │   │   ├── UsuarioBffController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── service/
│   │   │   │   ├── BebidaBffService.java
│   │   │   │   ├── ExperienciaBffService.java
│   │   │   │   ├── FeedBffService.java
│   │   │   │   ├── NotificacaoBffService.java
│   │   │   │   ├── StorageBffService.java         ← Upload para AWS S3
│   │   │   │   └── UsuarioBffService.java
│   │   │   └── dto/
│   │   │       ├── recive/                        ← DTOs recebidos da Core API
│   │   │       │   ├── FeedItemDTORecive.java
│   │   │       │   └── PaginaBffDTORecive.java
│   │   │       └── response/                      ← DTOs enviados para o Flutter
│   │   │           ├── ExploreResponseDTO.java
│   │   │           ├── FeedResponseDTO.java
│   │   │           ├── NotificacaoResponseDTO.java
│   │   │           ├── PerfilResponseDTO.java
│   │   │           └── RegistroExperienciaDTO.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── …/SipLogBFFApplicationTests.java
├── Dockerfile
├── Dockerfile.bff-local
├── mvnw / mvnw.cmd
└── pom.xml
```

---

## Endpoints Expostos ao Mobile

Base path: **`/api/v1/`** · Porta: **`8081`**

Toda requisição deve incluir o header: `Authorization: Bearer <access_token>`

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/usuarios/sync` | Sincroniza dados do JWT com a base (chamado após login) |
| `GET` | `/api/v1/usuarios/me` | Perfil do usuário autenticado |
| `PUT` | `/api/v1/usuarios/me` | Atualiza nome, bio e avatar |
| `DELETE` | `/api/v1/usuarios/me` | Remove a conta do usuário |
| `GET` | `/api/v1/usuarios/{id}` | Perfil público de outro usuário |
| `GET` | `/api/v1/usuarios/{id}/seguidores` | Lista de seguidores/seguindo (paginado) |
| `POST` | `/api/v1/usuarios/{id}/seguir` | Seguir ou deixar de seguir |

### Feed

| Método | Endpoint | Query Params | Descrição |
|---|---|---|---|
| `GET` | `/api/v1/feed/me` | `?pagina=0` | Feed global (todos os usuários) |
| `GET` | `/api/v1/feed/global` | `?pagina=0` | Explore — postagens públicas |
| `GET` | `/api/v1/feed/amigos` | `?pagina=0` | Apenas de quem você segue |
| `GET` | `/api/v1/feed/usuarios/{id}` | `?pagina=0` | Postagens de um usuário específico |

O BFF enriquece cada item de feed com campos calculados:
- `tempoDecorrido` — "Agora mesmo", "Há 2h", "Há 3 dias", etc.
- `curtidoPorMim` — booleano
- `totalCurtidas`, `totalComentarios`

### Experiências

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/experiencias` | Cria nova experiência |
| `PUT` | `/api/v1/experiencias/{id}` | Edita experiência existente |
| `DELETE` | `/api/v1/experiencias/{id}` | Remove experiência |
| `POST` | `/api/v1/experiencias/{id}/curtir` | Toggle curtida |
| `GET` | `/api/v1/experiencias/{id}/comentarios` | Lista comentários |
| `POST` | `/api/v1/experiencias/{id}/comentarios` | Adiciona comentário |
| `PUT` | `/api/v1/experiencias/{id}/comentarios/{idComentario}` | Edita comentário |
| `DELETE` | `/api/v1/experiencias/{id}/comentarios/{idComentario}` | Remove comentário |

### Bebidas

| Método | Endpoint | Query Params | Descrição |
|---|---|---|---|
| `GET` | `/api/v1/bebidas/buscar` | `?nome=...` | Autocomplete por nome |
| `GET` | `/api/v1/bebidas/{id}` | — | Detalhes completos |
| `POST` | `/api/v1/bebidas` | — | Cadastra nova bebida |

### Upload

| Método | Endpoint | Content-Type | Descrição |
|---|---|---|---|
| `POST` | `/api/v1/upload` | `multipart/form-data` | Envia arquivo para S3; retorna URL pública |

**Exemplo de resposta:**
```json
{ "url": "https://s3.amazonaws.com/sip-bucket/uploads/uuid.jpg" }
```

### Notificações

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/notificacoes` | Lista todas as notificações do usuário |
| `GET` | `/api/v1/notificacoes/nao-lidas/count` | `{ "total": 3 }` |
| `PATCH` | `/api/v1/notificacoes/{id}/lida` | Marca notificação como lida |

---

## Fluxo de uma Requisição

Exemplo: `POST /api/v1/experiencias`

```
Flutter
  │
  │  POST /api/v1/experiencias
  │  Authorization: Bearer <JWT>
  │  Body: { "bebidaId": 1, "nota": 8.5, "comentario": "Excelente!", "fotoPostUrl": "...", ... }
  ▼
BFF (ExperienciaBffController)
  │
  ├─ SecurityConfig valida o JWT com o Keycloak
  ├─ Extrai keycloakId do @AuthenticationPrincipal Jwt
  ├─ Chama ExperienciaBffService.criarExperiencia()
  │     └─ Monta NovaExperienciaDTO com o keycloakId
  │     └─ RestClient.post("/apiCore/v1/experiencias")
  │           + Header: Authorization: Bearer <JWT> (TokenRelayInterceptor)
  ▼
Core API (ExperienciaCoreController)
  │
  ├─ Valida JWT
  ├─ Busca usuário pelo keycloakId
  ├─ Persiste Experiencia no PostgreSQL
  └─ Dispara NotificacaoEvent para notificar seguidores
  ▼
BFF → Flutter: 201 Created
```

---

## Segurança — JWT e Keycloak

O BFF é configurado como **OAuth2 Resource Server** no Spring Security:

```yaml
# application.yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_SERVER_URL}
```

O `TokenRelayInterceptor` injeta o token do usuário em todas as chamadas internas:

```java
// TokenRelayInterceptor.java (simplificado)
@Override
public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
        request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
    }
    return execution.execute(request, body);
}
```

---

## Integração AWS S3

O `StorageBffService` recebe um `MultipartFile` do Flutter e realiza o upload ao S3:

1. Gera um nome de arquivo único com UUID
2. Usa o AWS SDK v2 (`S3Client`) para fazer o `PutObjectRequest`
3. Retorna a URL pública do objeto criado

**Configuração via `S3Config.java`:**
```yaml
# Variáveis de ambiente necessárias
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
AWS_S3_BUCKET_NAME=nome-do-bucket
AWS_REGION=us-east-1
```

---

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `KEYCLOAK_SERVER_URL` | URL completa do realm: `http://host:8080/realms/BFF` | Obrigatório |
| `CORE_API_URL` | URL base da Core API | `http://localhost:8082` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | Obrigatório para upload |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS | Obrigatório para upload |
| `AWS_SESSION_TOKEN` | Token de sessão (para credenciais temporárias) | Opcional |
| `AWS_S3_BUCKET_NAME` | Nome do bucket S3 | Obrigatório para upload |
| `AWS_REGION` | Região AWS (ex: `us-east-1`) | Obrigatório para upload |

---

## Como Executar

### Pré-condições

- Keycloak rodando e configurado (Realm `BFF`, Client `sipLog`)
- Core API rodando na porta `8082`
- Variáveis de ambiente configuradas

### Executar em modo desenvolvimento

```bash
cd sipLog-BFF

export KEYCLOAK_SERVER_URL=http://localhost:8080/realms/BFF
export CORE_API_URL=http://localhost:8082
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_S3_BUCKET_NAME=meu-bucket
export AWS_REGION=us-east-1

./mvnw spring-boot:run
```

O BFF iniciará na **porta 8081**.

### Alternativa: via Docker Compose

```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d bff-local
```

---

## Build e Docker

### Gerar o JAR

```bash
cd sipLog-BFF
./mvnw clean package -DskipTests
```

O JAR gerado fica em `target/sipLogBFF-0.0.1-SNAPSHOT.jar`.

### Dockerfiles disponíveis

| Arquivo | Uso |
|---|---|
| `Dockerfile` | Build de produção (multi-stage, otimizado) |
| `Dockerfile.bff-local` | Build para ambiente local (aceita rebuild incremental) |

```bash
docker build -t siplog-bff:latest -f Dockerfile .
```

---

## Swagger UI

Em ambiente local, a documentação interativa dos endpoints está disponível em:

```
http://localhost:8081/swagger-ui/index.html
```

Os contratos OpenAPI gerados automaticamente pelo **SpringDoc** (`springdoc-openapi-starter-webmvc-ui 2.8.0`) ficam disponíveis também via:

```
http://localhost:8081/v3/api-docs
```

Contratos versionados e exportados estão em:
📁 [`DocumentacaoProjeto/ContratosEndPoint/BFF/`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/ContratosEndPoint/BFF/)

---

> **Porta:** `8081`
> **Consome:** Core API (porta `8082`) · Keycloak (porta `8080`) · AWS S3
> **Consumido por:** App Flutter (mobile)
