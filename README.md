# 🍷 SipLog

> **Plataforma social de registro e compartilhamento de experiências com bebidas** — Projeto acadêmico da disciplina de Engenharia de Software — Universidade Mackenzie.

"Sip" (gole, em inglês) + "Log" (registro) = um diário social para apreciadores de bebidas.

---

## Sumário

1. [Funcionalidades](#-funcionalidades)
2. [Arquitetura](#-arquitetura)
3. [Estrutura do Repositório](#-estrutura-do-repositório)
4. [Modelo de Dados](#-modelo-de-dados)
5. [Stack Tecnológica](#-stack-tecnológica)
6. [Pré-requisitos](#-pré-requisitos)
7. [Como Executar e Build Local](#-como-executar-e-build-local)
8. [Como Rodar o Front (Mobile)](#-como-rodar-o-front-mobile)
9. [Contratos de API](#-contratos-de-api)

---

## 🚀 Funcionalidades

- **Autenticação segura** via Keycloak (OAuth2/OIDC) com suporte a auto-login e renovação automática de token
- **Registro de Experiências ("Sips")** — registre bebidas com nota, comentário, localização e foto
- **Feed Social** — visualize postagens de todos os usuários (Feed Global) ou apenas de quem você segue (Feed de Amigos)
- **Curtidas e Comentários** — interaja com as experiências de outros usuários (curtir/descurtir, comentar, editar e deletar comentários)
- **Rede de Amigos** — siga e deixe de seguir usuários, veja listas de seguidores e seguindo
- **Busca de Usuários** — encontre pessoas por nome
- **Notificações** — alertas de curtidas, comentários e novos seguidores, com contagem de não lidas
- **Catálogo de Bebidas** — busca por nome com autocomplete, cadastro colaborativo e atributos dinâmicos (JSONB)
- **Upload de Fotos** — envio de avatar e imagens de experiências para AWS S3
- **Integração com API externa de vinhos** — módulo `Api_vinhos` para consulta de catálogo externo (WineAPI)
- **Temas claro e escuro** no app mobile

---

## 🏛️ Arquitetura

O sistema segue uma arquitetura de **microsserviços com o padrão BFF (Backend for Frontend)**:

```
┌────────────────────────────────────────────────┐
│             Mobile (Flutter)                   │
│         iOS / Android / Web / Desktop          │
└──────────────────┬─────────────────────────────┘
                   │ REST + JWT (Bearer Token)
                   ▼
┌────────────────────────────────────────────────┐
│           sipLog-BFF  (Spring Boot)            │
│  Porta 8081 | Única entrada para o mobile      │
│  • Valida JWT (Keycloak Resource Server)       │
│  • Token Relay para a Core API                 │
│  • Upload de mídia para AWS S3                 │
│  • Agrega e formata dados para o Flutter       │
└──────────┬───────────────────────────────────┬─┘
           │ REST interno                       │ REST interno
           ▼                                   ▼
┌──────────────────────┐         ┌─────────────────────────────┐
│  Core API            │         │  Keycloak (IAM)             │
│  (Spring Boot)       │         │  Porta 8080                 │
│  Porta 8082          │         │  Identity & Access Mgmt     │
│  • Regras de negócio │         │  Realm: BFF                 │
│  • CRUD completo     │         │  Client ID: sipLog          │
│  • Eventos (notif.)  │         └─────────────────────────────┘
└──────────┬───────────┘
           │ JDBC/JPA
           ▼
┌────────────────────────────────────────────────┐
│          PostgreSQL 15 (Docker)                │
│  tb_usuario, tb_bebida, tb_experiencia,        │
│  tb_curtida, tb_comentario, tb_seguidor,       │
│  tb_notificacao                                │
└────────────────────────────────────────────────┘

                          ┌───────────────────────┐
                          │  AWS S3               │
                          │  Armazenamento de     │
                          │  fotos e avatares     │
                          └───────────────────────┘
```

**Fluxo de autenticação:**
1. O Flutter autentica diretamente no **Keycloak** via `flutter_appauth` (OIDC Authorization Code Flow)
2. O Keycloak retorna um **JWT (Access Token)**
3. Todas as chamadas ao **BFF** levam o JWT no header `Authorization: Bearer <token>`
4. O BFF valida o token e repassa a identidade do usuário para a **Core API** via `TokenRelayInterceptor`

---

## 📁 Estrutura do Repositório

```
sipLog/
├── core_api/                  → Microsserviço principal: domínio, regras de negócio e persistência
│   └── apiCore-sipLog/        → Projeto Spring Boot
├── sipLog-BFF/                → Backend for Frontend: porta de entrada do app mobile
├── Api_vinhos/                → Script/módulo de integração com API externa de vinhos (WineAPI)
├── mobile/                    → Aplicativo Flutter
│   └── front/sip_log_mobile/  → Projeto Flutter
├── infra/                     → Docker Compose (dev local e produção) + Jenkinsfile CI/CD
│   ├── localRun/              → Ambiente de desenvolvimento local
│   └── prodRun/               → Configurações de produção (AWS)
├── DocumentacaoProjeto/       → Diagramas UML, contratos OpenAPI, protótipos UI/UX
├── .gitignore
├── LICENSE                    → GPL-3.0
└── README.md
```

> Cada pasta possui seu próprio `README.md` detalhado. Navegue pelos links abaixo:
>
> - 📦 [core_api](https://github.com/Mendes1801/sipLog/tree/main/core_api) — API principal
> - 🛡️ [sipLog-BFF](https://github.com/Mendes1801/sipLog/tree/main/sipLog-BFF) — Backend for Frontend
> - 🍷 [Api_vinhos](https://github.com/Mendes1801/sipLog/tree/main/Api_vinhos) — Integração externa de vinhos
> - 📱 [mobile](https://github.com/Mendes1801/sipLog/tree/main/mobile) — App Flutter
> - 🐳 [infra](https://github.com/Mendes1801/sipLog/tree/main/infra) — Infraestrutura Docker
> - 📚 [DocumentacaoProjeto](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto) — Documentação técnica

---

## 📊 Modelo de Dados

Entidades principais persistidas no PostgreSQL pela `core_api`:

```
┌─────────────┐    ┌──────────────────┐    ┌─────────────────────┐
│  tb_usuario │    │  tb_experiencia  │    │     tb_bebida       │
├─────────────┤    ├──────────────────┤    ├─────────────────────┤
│ id          │1──*│ id               │*──1│ id                  │
│ keycloakId  │    │ usuario_id (FK)  │    │ nome                │
│ nome        │    │ bebida_id (FK)   │    │ categoria           │
│ username    │    │ nota: Double     │    │ fabricante          │
│ bio         │    │ comentario       │    │ caracteristicas JSONB│
│ fotoAvatarUrl│   │ fotoPostUrl      │    └─────────────────────┘
│ email       │    │ localizacao      │
└─────────────┘    │ visibilidade     │    ┌─────────────────────┐
       │           │ dataCriacao      │    │   tb_seguidor       │
       │           │ totalCurtidas*   │    ├─────────────────────┤
       │           │ totalComentarios*│    │ seguidor_id (FK)    │
       │           └──────────────────┘    │ seguido_id (FK)     │
       │                    │              └─────────────────────┘
       │           ┌────────┴─────────┐
       │           │                  │
  ┌────▼──────┐ ┌──▼────────────┐ ┌──▼──────────────┐
  │tb_curtida │ │tb_comentario  │ │tb_notificacao   │
  ├───────────┤ ├───────────────┤ ├─────────────────┤
  │usuario_id │ │usuario_id     │ │usuario_id       │
  │experiencia│ │experiencia_id │ │tipo (enum)      │
  │_id        │ │texto          │ │lida: boolean    │
  └───────────┘ │dataCriacao    │ │dataCriacao      │
                └───────────────┘ └─────────────────┘
```

> `*` — Campos calculados via `@Formula` (SQL inline do Hibernate), não persistidos diretamente.
> A coluna `caracteristicas` da `tb_bebida` usa o tipo JSONB do PostgreSQL para suportar atributos dinâmicos (IBU para cerveja, safra para vinho, etc.).

---

## 💻 Stack Tecnológica

| Camada | Tecnologia | Versão |
|---|---|---|
| Mobile | Flutter + Dart | SDK ^3.11.5 |
| Backend (Core API + BFF) | Java + Spring Boot | Java 21 / Spring Boot 4.x |
| Build backend | Maven Wrapper (`./mvnw`) | 3.8+ |
| Identity Provider | Keycloak | 24.0.0 |
| Storage de mídia | AWS S3 (SDK v2) | 2.25.20 |
| Banco de dados | PostgreSQL | 15-alpine |
| Containerização | Docker + Docker Compose | v2+ |
| CI/CD | Jenkins (Jenkinsfile) | — |
| Documentação de API | SpringDoc OpenAPI (Swagger) | 2.8.0 |

---

## ⚙️ Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [ ] **Java 21 (JDK)** — [Adoptium Temurin](https://adoptium.net/)
- [ ] **Flutter SDK** (Dart incluído) — [Instalação oficial](https://flutter.dev/docs/get-started/install)
- [ ] **Docker e Docker Compose v2** — [Docker Desktop](https://www.docker.com/)
- [ ] **Git** — [git-scm.com](https://git-scm.com/)

> Não é necessário instalar Maven separadamente: os projetos Java usam o **Maven Wrapper** (`./mvnw`).

---

## 🛠️ Como Executar e Build Local

A execução completa requer subir a infraestrutura base (PostgreSQL + Keycloak) e depois os serviços Java.

### 1. Configurar variáveis de ambiente

Na pasta `infra/`, copie o arquivo de exemplo e preencha as variáveis:

```bash
cp infra/.env.exemple infra/.env
```

Edite o `infra/.env` com os valores para desenvolvimento local. Veja o detalhamento completo em [infra/README.md](https://github.com/Mendes1801/sipLog/tree/main/infra).

### 2. Subir a infraestrutura (PostgreSQL + Keycloak)

```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d
```

Aguarde o Keycloak inicializar completamente (pode levar ~30–60 segundos). Acesse `http://localhost:8080` para verificar. Configure o Realm `BFF` e o Client `sipLog` pelo painel de administração do Keycloak.

### 3. Executar a Core API

```bash
cd core_api/apiCore-sipLog
./mvnw spring-boot:run
```

A Core API iniciará na **porta 8082**.

### 4. Executar o BFF

Configure as variáveis de ambiente da AWS (S3) antes de rodar:

```bash
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_S3_BUCKET_NAME=...
export AWS_REGION=...
export CORE_API_URL=http://localhost:8082
export KEYCLOAK_SERVER_URL=http://localhost:8080/realms/BFF

cd sipLog-BFF
./mvnw spring-boot:run
```

O BFF iniciará na **porta 8081**.

### Build Local (gerar JAR)

```bash
# Core API
cd core_api/apiCore-sipLog
./mvnw clean package -DskipTests
java -jar target/apiCore-sipLog-*.jar

# BFF
cd sipLog-BFF
./mvnw clean package -DskipTests
java -jar target/sipLogBFF-*.jar
```

### Build Completo via Docker (local)

O `docker-compose-local.yml` também constrói e sobe os serviços Java automaticamente:

```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d --build
```

---

## 📱 Como Rodar o Front (Mobile)

O aplicativo Flutter se comunica com o BFF via REST e com o Keycloak via OIDC.

```bash
cd mobile/front/sip_log_mobile

# 1. Instalar dependências Dart/Flutter
flutter pub get

# 2. Verificar ambiente
flutter doctor

# 3. Listar dispositivos disponíveis
flutter devices

# 4. Rodar no dispositivo/emulador
flutter run
```

> A URL do BFF e do Keycloak estão configuradas em `lib/services/http_api_service.dart` e `lib/services/auth_service.dart`. Veja os detalhes em [mobile/README.md](https://github.com/Mendes1801/sipLog/tree/main/mobile).

Para mais detalhes, builds de produção e configuração de ambiente, consulte [mobile/README.md](https://github.com/Mendes1801/sipLog/tree/main/mobile).

---

## 📄 Contratos de API

Os contratos OpenAPI (Swagger) da comunicação entre o Mobile e o BFF estão documentados e versionados em:

📁 [`DocumentacaoProjeto/ContratosEndPoint/BFF/`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/ContratosEndPoint/BFF/)

Arquivos disponíveis: `api-docsV2.json`, `api-docsV6.json`, `api-docsV7.json`, `api-docsV8.json`

Em desenvolvimento local, o Swagger UI do BFF fica disponível em:
`http://localhost:8081/swagger-ui/index.html`

| Serviço | Porta | Base Path |
|---|---|---|
| Keycloak | `8080` | `/realms/BFF` |
| BFF (entrada do mobile) | `8081` | `/api/v1/` |
| Core API (interna) | `8082` | `/apiCore/v1/` |

Para detalhes dos endpoints de cada serviço, consulte os READMEs individuais:
- [core_api/README.md](https://github.com/Mendes1801/sipLog/tree/main/core_api)
- [sipLog-BFF/README.md](https://github.com/Mendes1801/sipLog/tree/main/sipLog-BFF)

---

> Projeto desenvolvido como trabalho acadêmico na disciplina de **Engenharia de Software** — Universidade Presbiteriana Mackenzie.
