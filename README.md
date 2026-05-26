# 🍷 SipLog

> **Plataforma social de registro e compartilhamento de experiências com bebidas**
> Projeto acadêmico da disciplina de Engenharia de Software — Universidade Presbiteriana Mackenzie.

"Sip" (gole, em inglês) + "Log" (registro) = um diário social para apreciadores de bebidas.

---

## Sumário

1. [Funcionalidades](#-funcionalidades)
2. [Arquitetura](#-arquitetura)
3. [Estrutura do Repositório](#-estrutura-do-repositório)
4. [Modelo de Dados](#-modelo-de-dados)
5. [Stack Tecnológica](#-stack-tecnológica)
6. [Pré-requisitos](#-pré-requisitos)
7. [Como Executar — Build Local](#-como-executar--build-local)
8. [Como Rodar o Front (Mobile)](#-como-rodar-o-front-mobile)
9. [Contratos de API](#-contratos-de-api)
10. [CI/CD e Produção](#-cicd-e-produção)
11. [Documentação Técnica](#-documentação-técnica)
12. [Licença](#-licença)

---

## 🚀 Funcionalidades

| Funcionalidade | Descrição |
|---|---|
| **Autenticação via Keycloak** | Login seguro com OAuth2/OIDC; suporte a auto-login e renovação automática de token |
| **Registro de Experiências ("Sips")** | Registre bebidas com nota (0–10), comentário, localização e foto |
| **Feed Global** | Visualize postagens públicas de todos os usuários com paginação |
| **Feed de Amigos** | Veja apenas as experiências de quem você segue |
| **Curtidas e Comentários** | Curtir/descurtir, comentar, editar e excluir comentários |
| **Rede de Amigos** | Seguir/deixar de seguir usuários, listar seguidores e seguindo |
| **Busca de Usuários** | Encontre pessoas por nome |
| **Notificações** | Alertas de curtidas, comentários e novos seguidores; contagem de não lidas |
| **Catálogo de Bebidas** | Busca com autocomplete, cadastro colaborativo e atributos dinâmicos (JSONB) |
| **Upload de Fotos** | Envio de avatar e imagens de experiências para AWS S3 |
| **Integração WineAPI** | Módulo `Api_vinhos` para consulta a catálogo externo de vinhos |
| **Temas claro e escuro** | Preferência de tema persistida no app mobile |
| **Controle de Visibilidade** | Cada experiência pode ser `PUBLICA`, `AMIGOS` ou `PRIVADA` |

---

## 🏛️ Arquitetura

O sistema adota uma arquitetura de **microsserviços com o padrão BFF (Backend for Frontend)**:

```
┌──────────────────────────────────────────────────┐
│              Mobile (Flutter)                    │
│         iOS · Android · Web · Desktop            │
└──────────────────┬───────────────────────────────┘
                   │ HTTPS · REST · JWT Bearer Token
                   ▼
┌──────────────────────────────────────────────────┐
│           sipLog-BFF  (Spring Boot)              │
│  Porta 8081 — único ponto de entrada do mobile   │
│                                                  │
│  • Valida JWT (Keycloak Resource Server)         │
│  • Token Relay para a Core API                   │
│  • Upload de mídia para AWS S3                   │
│  • Agrega e formata dados para o Flutter         │
└───────────┬───────────────────────────┬──────────┘
            │ REST interno              │ REST interno
            ▼                           ▼
┌───────────────────────┐   ┌───────────────────────────┐
│  Core API             │   │  Keycloak (IAM)           │
│  (Spring Boot)        │   │  Porta 8080               │
│  Porta 8082           │   │  Realm: BFF               │
│                       │   │  Client ID: sipLog        │
│  • Regras de negócio  │   └───────────────────────────┘
│  • CRUD completo      │
│  • Eventos (notif.)   │
└──────────┬────────────┘
           │ JDBC / JPA
           ▼
┌──────────────────────────────────────────────────┐
│              PostgreSQL 15 (Docker)              │
│  tb_usuario · tb_bebida · tb_experiencia         │
│  tb_curtida · tb_comentario · tb_seguidor        │
│  tb_notificacao                                  │
└──────────────────────────────────────────────────┘

                        ┌───────────────────────────┐
                        │        AWS S3             │
                        │  Armazenamento de fotos   │
                        │  e avatares               │
                        └───────────────────────────┘
```

### Fluxo de autenticação passo a passo

```
Flutter ──(1. Authorization Code Flow)──► Keycloak
Keycloak ──(2. JWT Access Token)──────── ► Flutter
Flutter ──(3. Bearer <token>)────────────► BFF
BFF ──(4. Valida JWT no Keycloak)
BFF ──(5. TokenRelay: repassa identidade)─► Core API
Core API ──(6. Resposta de dados)─────────► BFF
BFF ──(7. JSON formatado para Flutter)──── ► Flutter
```

> O app Flutter utiliza `flutter_appauth` para o fluxo OIDC. O BFF atua como **Resource Server**: nunca armazena credenciais; apenas valida o token e repassa a identidade via `TokenRelayInterceptor`.

---

## 📁 Estrutura do Repositório

```
sipLog/                          ← Raiz do monorepo
│
├── core_api/                    ← Microsserviço principal (domínio e persistência)
│   └── apiCore-sipLog/          ← Projeto Spring Boot (porta 8082)
│       ├── src/main/java/…      ← Código-fonte (controller, service, repository, entity, dto)
│       ├── Dockerfile
│       └── pom.xml
│
├── sipLog-BFF/                  ← Backend for Frontend (porta 8081)
│   ├── src/main/java/…          ← Código-fonte (controller, service, dto, config)
│   ├── Dockerfile
│   └── pom.xml
│
├── Api_vinhos/                  ← Módulo de integração com WineAPI externa
│   ├── API_Wineapi.java
│   └── pom.xml
│
├── mobile/                      ← Aplicativo Flutter
│   └── front/sip_log_mobile/
│       ├── lib/                 ← Código Dart (models, screens, services, widgets)
│       ├── assets/              ← Fontes e imagens
│       ├── android/             ← Configuração nativa Android
│       ├── ios/                 ← Configuração nativa iOS
│       └── pubspec.yaml
│
├── infra/                       ← Infraestrutura Docker e CI/CD
│   ├── .env.exemple             ← Template de variáveis de ambiente
│   ├── localRun/
│   │   └── docker-compose-local.yml   ← Ambiente de desenvolvimento completo
│   └── prodRun/
│       ├── docker-compose-bds.yml     ← Banco de dados + Keycloak (produção)
│       ├── docker-compose-spring.yml  ← Serviços Java (produção)
│       ├── docker-compose-prod.yml    ← Stack completa (produção)
│       └── Jenkinsfile                ← Pipeline CI/CD
│
├── DocumentacaoProjeto/         ← Diagramas UML, contratos OpenAPI, assets visuais
│   ├── Diagramas/scr/           ← Mermaid/PlantUML (classes, atividades, sequência, componentes)
│   └── ContratosEndPoint/BFF/   ← Contratos OpenAPI versionados (api-docsV2~V8.json)
│
├── .gitignore
├── LICENSE                      ← GPL-3.0
└── README.md                    ← Este arquivo
```

> Cada subpasta possui seu próprio `README.md` com detalhes específicos:
>
> - 📦 [core_api](https://github.com/Mendes1801/sipLog/tree/main/core_api) — Microsserviço principal
> - 🛡️ [sipLog-BFF](https://github.com/Mendes1801/sipLog/tree/main/sipLog-BFF) — Backend for Frontend
> - 🍷 [Api_vinhos](https://github.com/Mendes1801/sipLog/tree/main/Api_vinhos) — Integração externa de vinhos
> - 📱 [mobile](https://github.com/Mendes1801/sipLog/tree/main/mobile) — App Flutter
> - 🐳 [infra](https://github.com/Mendes1801/sipLog/tree/main/infra) — Infraestrutura Docker e CI/CD
> - 📚 [DocumentacaoProjeto](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto) — Documentação técnica

---

## 📊 Modelo de Dados

Entidades persistidas no PostgreSQL pela `core_api`:

```
┌──────────────┐     ┌───────────────────┐     ┌────────────────────┐
│  tb_usuario  │     │  tb_experiencia   │     │    tb_bebida       │
├──────────────┤     ├───────────────────┤     ├────────────────────┤
│ id (PK)      │1──*│ id (PK)           │*──1│ id (PK)            │
│ keycloakId   │     │ usuario_id (FK)   │     │ nome               │
│ nome         │     │ bebida_id (FK)    │     │ categoria          │
│ username     │     │ nota: Double      │     │ fabricante         │
│ bio          │     │ comentario: Text  │     │ caracteristicas    │
│ fotoAvatarUrl│     │ fotoPostUrl       │     │   (JSONB) ◄──────  │
│ email        │     │ localizacao       │     └────────────────────┘
└──────────────┘     │ visibilidade      │
       │1            │   (enum)          │
       │             │ dataCriacao       │
       │             │ totalCurtidas*    │
       │             │ totalComentarios* │
       │             └───────────────────┘
       │                      │1
  ┌────┴───────┐    ┌──────────┴──────┐    ┌──────────────────┐
  │tb_seguidor │    │   tb_curtida    │    │  tb_comentario   │
  ├────────────┤    ├─────────────────┤    ├──────────────────┤
  │seguidor_id │    │ usuario_id (FK) │    │ usuario_id (FK)  │
  │  (FK)      │    │ experiencia_id  │    │ experiencia_id   │
  │seguido_id  │    │   (FK)          │    │   (FK)           │
  │  (FK)      │    └─────────────────┘    │ texto            │
  └────────────┘                           │ dataCriacao      │
                                           └──────────────────┘

  ┌─────────────────────┐
  │   tb_notificacao    │
  ├─────────────────────┤
  │ usuario_id (FK)     │
  │ tipo (enum)         │  ← CURTIDA | COMENTARIO | NOVO_SEGUIDOR
  │ lida: boolean       │
  │ dataCriacao         │
  └─────────────────────┘
```

**Notas:**
- `*` — Campos `totalCurtidas` e `totalComentarios` são calculados por `@Formula` (SQL inline do Hibernate), não são colunas físicas.
- `caracteristicas` em `tb_bebida` usa o tipo **JSONB** nativo do PostgreSQL, permitindo atributos dinâmicos por categoria (ex.: `{ "ibu": "40", "teor_alcoolico": "5.2" }` para cervejas; `{ "safra": "2019", "uva": "Malbec" }` para vinhos).
- A tabela `tb_usuario` usa `keycloakId` (UUID gerado pelo Keycloak) como identificador externo, desacoplando o domínio do IAM.
- DDL é gerenciado automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto: update`).

---

## 💻 Stack Tecnológica

| Camada | Tecnologia | Versão |
|---|---|---|
| Mobile | Flutter + Dart | SDK `^3.11.5` |
| Backend (BFF + Core API) | Java + Spring Boot | Java 21 / Spring Boot 4.x |
| Build Java | Maven Wrapper (`./mvnw`) | Maven 3.8+ |
| Autenticação / IAM | Keycloak | 24.0.0 |
| Storage de Mídia | AWS S3 (SDK v2) | `2.25.20` |
| Banco de Dados | PostgreSQL | 15-alpine |
| ORM | Hibernate / Spring Data JPA | — |
| Containerização | Docker + Docker Compose v2 | — |
| CI/CD | Jenkins (Declarative Pipeline) | — |
| Análise de Qualidade | SonarQube | — |
| Documentação de API | SpringDoc OpenAPI (Swagger UI) | `2.8.0` |
| Fonte customizada | BaksoSapi (OTF) | — |

### Dependências principais do Flutter (`pubspec.yaml`)

| Pacote | Propósito |
|---|---|
| `flutter_appauth` | Fluxo OIDC com Keycloak |
| `flutter_secure_storage` | Armazenamento seguro de tokens |
| `http` | Chamadas REST ao BFF |
| `provider` | Gerenciamento de estado |
| `flutter_map` + `latlong2` | Mapa de localização |
| `image_picker` | Seleção de fotos |
| `url_launcher` | Abrir links externos |

---

## ⚙️ Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [ ] **Java 21 (JDK)** — [Adoptium Temurin](https://adoptium.net/)
- [ ] **Flutter SDK** (Dart incluído) — [Instalação oficial](https://flutter.dev/docs/get-started/install)
- [ ] **Docker e Docker Compose v2** — [Docker Desktop](https://www.docker.com/)
- [ ] **Git** — [git-scm.com](https://git-scm.com/)

> Maven **não** precisa ser instalado separadamente: todos os projetos Java usam o **Maven Wrapper** (`./mvnw`).

---

## 🛠️ Como Executar — Build Local

A stack completa requer: PostgreSQL + Keycloak (via Docker) → Core API → BFF → Flutter.

### 1. Clone o repositório

```bash
git clone https://github.com/Mendes1801/sipLog.git
cd sipLog
```

### 2. Configure as variáveis de ambiente

```bash
cp infra/.env.exemple infra/.env
```

Edite `infra/.env` com os valores locais. Variáveis obrigatórias:

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_NAME` | Nome do banco de dados | `sip_db` |
| `DB_USER` | Usuário do PostgreSQL | `sip_user` |
| `DB_PASSWORD` | Senha do PostgreSQL | `senha_local` |
| `KC_DB_NAME` | Banco do Keycloak | `kc_db` |
| `KC_DB_USER` | Usuário do banco Keycloak | `kc_user` |
| `KC_DB_PASSWORD` | Senha do banco Keycloak | `kc_senha` |
| `KC_ADMIN_USER` | Usuário admin do Keycloak | `admin` |
| `KC_ADMIN_PASSWORD` | Senha admin do Keycloak | `admin123` |
| `KC_REALM` | Nome do Realm | `BFF` |
| `KC_BACKEND_URL` | URL interna do Keycloak | `http://localhost:8080` |
| `CORE_API_URL` | URL da Core API vista pelo BFF | `http://localhost:8082` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | — |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS | — |
| `AWS_S3_BUCKET_NAME` | Nome do bucket S3 | — |
| `AWS_REGION` | Região AWS | `us-east-1` |

### 3. Suba a infraestrutura base (PostgreSQL + Keycloak)

```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d
```

Aguarde ~30–60 segundos para o Keycloak inicializar completamente.
Acesse `http://localhost:8080` e entre com as credenciais admin configuradas no `.env`.

**Configuração obrigatória no Keycloak:**
1. Crie o Realm `BFF`
2. Crie o Client `sipLog` com **Authorization Code Flow** habilitado
3. Configure o Redirect URI: `com.example.siplogmobile://oauth2redirect`
4. Habilite `Direct Access Grants` (para testes via curl/Postman)

### 4. Execute a Core API

```bash
cd core_api/apiCore-sipLog
./mvnw spring-boot:run
```

A Core API iniciará na **porta 8082**. Acompanhe o log — o Hibernate irá criar as tabelas automaticamente no primeiro boot.

### 5. Execute o BFF

```bash
cd sipLog-BFF

# Configure as variáveis de ambiente (se ainda não estiver no .env)
export CORE_API_URL=http://localhost:8082
export KEYCLOAK_SERVER_URL=http://localhost:8080/realms/BFF
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_S3_BUCKET_NAME=...
export AWS_REGION=...

./mvnw spring-boot:run
```

O BFF iniciará na **porta 8081**. O Swagger UI ficará disponível em:
`http://localhost:8081/swagger-ui/index.html`

### Build Local — Gerar JARs manualmente

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

### Build Completo via Docker Compose

O `docker-compose-local.yml` também constrói e sobe os serviços Java:

```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d --build
```

Isso sobe: `postgres-local` (5432) · `postgres-keycloak` · `keycloak` (8080) · `core-api-local` (8082) · `bff-local` (8081).

> Detalhes completos dos arquivos Docker e Compose em [infra/README.md](https://github.com/Mendes1801/sipLog/tree/main/infra).

---

## 📱 Como Rodar o Front (Mobile)

O aplicativo Flutter se comunica com o BFF via REST e com o Keycloak via OIDC.

### Pré-configuração

Antes de rodar, verifique as URLs nos arquivos:

- **BFF URL** → `mobile/front/sip_log_mobile/lib/services/http_api_service.dart`
  ```dart
  final String baseUrl = 'http://<IP_DO_SEU_BFF>:8081/api/v1';
  ```

- **Keycloak URL** → `mobile/front/sip_log_mobile/lib/services/auth_service.dart`
  ```dart
  static const String _issuer = 'http://<IP_DO_KEYCLOAK>:8080/realms/BFF';
  ```

> Em emuladores Android, use `10.0.2.2` para referenciar o `localhost` da máquina host. Em dispositivos físicos, use o IP da máquina na rede local.

### Executar o app

```bash
cd mobile/front/sip_log_mobile

# Instalar dependências Dart/Flutter
flutter pub get

# Verificar ambiente
flutter doctor

# Listar dispositivos disponíveis
flutter devices

# Rodar no dispositivo/emulador escolhido
flutter run

# Build release (APK Android)
flutter build apk --release

# Build release (iOS — necessita macOS + Xcode)
flutter build ios --release
```

> Detalhes completos em [mobile/README.md](https://github.com/Mendes1801/sipLog/tree/main/mobile).

---

## 📄 Contratos de API

### Endereços dos serviços

| Serviço | Porta | Base Path | Swagger UI |
|---|---|---|---|
| Keycloak (IAM) | `8080` | `/realms/BFF` | `http://localhost:8080/admin` |
| **BFF** (entrada do mobile) | `8081` | `/api/v1/` | `http://localhost:8081/swagger-ui/index.html` |
| Core API (interna) | `8082` | `/apiCore/v1/` | `http://localhost:8082/swagger-ui/index.html` |

### Endpoints BFF (`/api/v1/`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/usuarios/sync` | Sincroniza usuário após login no Keycloak |
| `GET` | `/usuarios/me` | Retorna o perfil do usuário autenticado |
| `PUT` | `/usuarios/me` | Atualiza nome, bio ou avatar |
| `DELETE` | `/usuarios/me` | Remove a conta |
| `GET` | `/usuarios/{id}` | Perfil público de outro usuário |
| `GET` | `/usuarios/{id}/seguidores` | Lista de seguidores/seguindo |
| `POST` | `/usuarios/{id}/seguir` | Seguir ou deixar de seguir |
| `GET` | `/feed/me` | Feed global paginado |
| `GET` | `/feed/global` | Feed de todos os usuários |
| `GET` | `/feed/amigos` | Feed apenas de quem o usuário segue |
| `GET` | `/feed/usuarios/{id}` | Feed de um usuário específico |
| `POST` | `/experiencias` | Cria nova experiência |
| `PUT` | `/experiencias/{id}` | Edita experiência |
| `DELETE` | `/experiencias/{id}` | Remove experiência |
| `POST` | `/experiencias/{id}/curtir` | Curtir/descurtir |
| `GET` | `/experiencias/{id}/comentarios` | Lista comentários |
| `POST` | `/experiencias/{id}/comentarios` | Adiciona comentário |
| `PUT` | `/experiencias/{id}/comentarios/{idComentario}` | Edita comentário |
| `DELETE` | `/experiencias/{id}/comentarios/{idComentario}` | Remove comentário |
| `GET` | `/bebidas/buscar` | Busca bebidas por nome |
| `GET` | `/bebidas/{id}` | Detalhes de uma bebida |
| `POST` | `/bebidas` | Cadastra nova bebida |
| `POST` | `/upload` | Upload de imagem para S3 |
| `GET` | `/notificacoes` | Lista notificações do usuário |
| `GET` | `/notificacoes/nao-lidas/count` | Contagem de notificações não lidas |
| `PATCH` | `/notificacoes/{id}/lida` | Marca notificação como lida |

Contratos OpenAPI completos versionados em:
📁 [`DocumentacaoProjeto/ContratosEndPoint/BFF/`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/ContratosEndPoint/BFF/)

---

## 🔄 CI/CD e Produção

O projeto utiliza um **Jenkinsfile declarativo** com as seguintes etapas:

```
Checkout → Build → Testes Unitários → SonarQube Analysis → Quality Gate
       → Package (JAR) → Build Docker Image → Push Registry → Deploy VM
```

Para subir em produção (AWS EC2), consulte o guia completo em [infra/README.md](https://github.com/Mendes1801/sipLog/tree/main/infra).

---

## 📚 Documentação Técnica

| Arquivo | Conteúdo |
|---|---|
| [`Diagramas/scr/diagrama_classes.md`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/Diagramas/scr/diagrama_classes.md) | Diagrama de classes UML (Mermaid/PlantUML) |
| [`Diagramas/scr/diagrama_atividades.md`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/Diagramas/scr/diagrama_atividades.md) | Fluxos de negócio |
| [`Diagramas/scr/diagrama_sequencia.md`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/Diagramas/scr/diagrama_sequencia.md) | Fluxo de chamadas entre componentes |
| [`Diagramas/scr/diagrama_componentes.md`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/Diagramas/scr/diagrama_componentes.md) | Visão dos microsserviços |
| [`ContratosEndPoint/BFF/api-docsV8.json`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/ContratosEndPoint/BFF/) | Contrato OpenAPI mais recente |

Detalhes completos em [DocumentacaoProjeto/README.md](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto).

---

## 📜 Licença

Distribuído sob a licença **GPL-3.0**. Consulte o arquivo [LICENSE](https://github.com/Mendes1801/sipLog/blob/main/LICENSE) para mais informações.

---

> Projeto desenvolvido como trabalho acadêmico na disciplina de **Engenharia de Software** — Universidade Presbiteriana Mackenzie.
