# 🐳 infra — Infraestrutura Docker e CI/CD

Este diretório contém todos os manifestos necessários para provisionar o ambiente do SipLog, desde o desenvolvimento local até a produção em cloud (AWS EC2), além do pipeline CI/CD via Jenkins.

---

## Sumário

1. [Visão Geral](#visão-geral)
2. [Configurar Variáveis de Ambiente](#configurar-variáveis-de-ambiente)
3. [Ambiente de Desenvolvimento Local (localRun)](#ambiente-de-desenvolvimento-local-localrun)
4. [Ambiente de Produção (prodRun)](#ambiente-de-produção-prodrun)
5. [Pipeline CI/CD — Jenkinsfile](#pipeline-cicd--jenkinsfile)
6. [Referência de Variáveis de Ambiente](#referência-de-variáveis-de-ambiente)

---

## Visão Geral

```
infra/
├── .env.exemple                    ← Template de variáveis de ambiente
│
├── localRun/
│   └── docker-compose-local.yml   ← Stack completa para desenvolvimento local
│       Sobe: postgres-local · postgres-keycloak · keycloak · core-api-local · bff-local
│
└── prodRun/
    ├── docker-compose-bds.yml      ← Bancos de dados em produção (PostgreSQL para app + Keycloak)
    ├── docker-compose-keycloak.yml ← Serviço Keycloak em produção
    ├── docker-compose-spring.yml   ← Core API + BFF em produção (com build Docker)
    ├── docker-compose-prod.yml     ← Stack monolítica de produção (alternativa)
    ├── Jenkinsfile                 ← Pipeline declarativo CI/CD
    └── README.md                  ← Guia de deploy em VM
```

---

## Configurar Variáveis de Ambiente

Todas as configurações sensíveis são injetadas via variáveis de ambiente. **Nunca commit o arquivo `.env`.**

```bash
# Na raiz do repositório
cp infra/.env.exemple infra/.env
```

Edite `infra/.env` com os valores para o seu ambiente. Veja a [referência completa](#referência-de-variáveis-de-ambiente) abaixo.

---

## Ambiente de Desenvolvimento Local (localRun)

O `docker-compose-local.yml` sobe a stack completa localmente:

| Serviço | Container | Porta | Imagem |
|---|---|---|---|
| PostgreSQL (app) | `sip_postgres_local` | `5432` | `postgres:15-alpine` |
| PostgreSQL (Keycloak) | `sip_postgres_keycloak` | — (interno) | `postgres:15-alpine` |
| Keycloak | `sip_keycloak_local` | `8080` | `keycloak:24.0.0` |
| Core API | `siplog-core-api-local` | `8082` | Build local (`Dockerfile.core-local`) |
| BFF | `siplog-bff-local` | `8081` | Build local (`Dockerfile.bff-local`) |

### Passo a passo

**1. Configure o `.env`**
```bash
cp infra/.env.exemple infra/.env
# Edite com seus valores locais
```

**2. Suba toda a stack**
```bash
cd infra/localRun
docker compose -f docker-compose-local.yml up -d
```

**3. Suba com rebuild das imagens Java (após mudanças no código)**
```bash
docker compose -f docker-compose-local.yml up -d --build
```

**4. Verifique os serviços**
```bash
docker compose -f docker-compose-local.yml ps
docker compose -f docker-compose-local.yml logs -f bff-local
```

**5. Acesse o Keycloak** — `http://localhost:8080`

Após o Keycloak iniciar (~30–60 segundos), configure:
- Crie o **Realm** `BFF`
- Crie o **Client** `sipLog`
  - Access Type: `public`
  - Valid Redirect URIs: `com.example.siplogmobile://oauth2redirect`
  - Web Origins: `*`
- Habilite **Direct Access Grants** para testes via Postman/curl

**6. Parar o ambiente**
```bash
docker compose -f docker-compose-local.yml down

# Para remover volumes (dados) também:
docker compose -f docker-compose-local.yml down -v
```

### Verificar saúde dos serviços

```bash
# Core API
curl http://localhost:8082/apiCore/v1/bebidas/buscar?nome=vinho

# BFF (deve retornar 401 sem token)
curl http://localhost:8081/api/v1/feed/global

# Keycloak
curl http://localhost:8080/realms/BFF/.well-known/openid-configuration
```

---

## Ambiente de Produção (prodRun)

A produção usa arquivos Docker Compose separados por responsabilidade, permitindo atualizações independentes de cada camada.

### Pré-requisito na VM

Crie a rede Docker compartilhada entre os serviços:

```bash
sudo docker network create sip-network
```

### Subir tudo pela primeira vez

```bash
cd infra/prodRun
sudo docker compose \
  -f docker-compose-bds.yml \
  -f docker-compose-keycloak.yml \
  -f docker-compose-spring.yml \
  up -d
```

### Atualizar apenas os serviços Spring (BFF e Core API)

Após um novo deploy (ex.: via Jenkins), apenas rebuide os serviços Java:

```bash
cd infra/prodRun
sudo docker compose -f docker-compose-spring.yml up -d --build
```

### Arquivos de produção

**`docker-compose-bds.yml`** — Banco de dados do SipLog:
- `sip_postgres` na porta `5432`
- Dados persistidos em volume local `./volume/postgres`

**`docker-compose-keycloak.yml`** — IAM:
- Keycloak 24.0.0 conectado ao PostgreSQL dedicado
- Configurado com `KC_FRONTEND_URL` e `KC_BACKEND_URL`

**`docker-compose-spring.yml`** — Serviços Java:
- `core-api` na porta `8082`
- `bff` na porta `8081`
- Ambos na rede `sip-network`

---

## Pipeline CI/CD — Jenkinsfile

O `Jenkinsfile` declara um pipeline com as seguintes etapas:

```
┌─────────────┐
│  Checkout   │  Clone do branch main
└──────┬──────┘
       │
┌──────▼──────┐
│    Build    │  ./mvnw clean compile (Core API + BFF)
└──────┬──────┘
       │
┌──────▼──────┐
│    Tests    │  ./mvnw test + publicação JUnit XML
└──────┬──────┘
       │
┌──────▼──────────┐
│ SonarQube       │  Análise estática de código
│ Analysis        │  Dois projetos: sipLog-core + sipLog-bff
└──────┬──────────┘
       │
┌──────▼──────┐
│ Quality     │  Aguarda resultado do Quality Gate (~5 min)
│   Gate      │  Aborta pipeline se reprovado
└──────┬──────┘
       │
┌──────▼──────┐
│  Package    │  ./mvnw package → gera JARs
└──────┬──────┘
       │
┌──────▼──────────┐
│  Docker Build   │  Constrói imagens Docker dos dois serviços
│  & Push         │  Envia ao registry configurado
└──────┬──────────┘
       │
┌──────▼──────┐
│   Deploy    │  SSH na VM de produção
│    VM       │  docker compose pull + up --force-recreate
└─────────────┘
```

### Ferramentas configuradas no Jenkins

| Ferramenta | ID no Jenkins |
|---|---|
| Maven | `Maven3` |
| JDK | `JDK21` |
| SonarQube Server | `SonarQube-Server` |

### Configurar credenciais no Jenkins

O pipeline usa as credenciais configuradas no Jenkins Credentials Store:
- SSH key da VM de produção
- Docker registry credentials (usuário/senha)

---

## Referência de Variáveis de Ambiente

Arquivo: `infra/.env` (criado a partir do `.env.exemple`)

### Banco de Dados — SipLog

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_NAME` | Nome do banco PostgreSQL | `sip_db` |
| `DB_USER` | Usuário do banco | `sip_user` |
| `DB_PASSWORD` | Senha do banco | `senha_forte` |

### Banco de Dados — Keycloak

| Variável | Descrição | Exemplo |
|---|---|---|
| `KC_DB_NAME` | Nome do banco do Keycloak | `kc_db` |
| `KC_DB_USER` | Usuário do banco do Keycloak | `kc_user` |
| `KC_DB_PASSWORD` | Senha do banco do Keycloak | `kc_senha` |

### Keycloak

| Variável | Descrição | Exemplo |
|---|---|---|
| `KC_ADMIN_USER` | Login do admin do Keycloak | `admin` |
| `KC_ADMIN_PASSWORD` | Senha do admin | `admin123` |
| `KC_REALM` | Nome do Realm | `BFF` |
| `KC_FRONTEND_URL` | URL pública do Keycloak (usada pelo browser) | `http://meu-dominio.com:8080` |
| `KC_BACKEND_URL` | URL interna do Keycloak (usada pelos serviços) | `http://keycloak:8080` |

### Serviços Spring Boot

| Variável | Descrição | Exemplo |
|---|---|---|
| `CORE_API_URL` | URL da Core API vista pelo BFF | `http://core-api:8082` |
| `SPRING_PROFILES_ACTIVE` | Profile ativo do Spring | `prod` |
| `JAVA_OPTS` | Opções JVM | `-Xmx256m -Xms128m` |

### AWS S3

| Variável | Descrição |
|---|---|
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS |
| `AWS_SESSION_TOKEN` | Token de sessão (credenciais temporárias) |
| `AWS_S3_BUCKET_NAME` | Nome do bucket |
| `AWS_REGION` | Região (ex.: `us-east-1`) |

---

> Para dúvidas sobre configuração do Keycloak, consulte a [documentação oficial](https://www.keycloak.org/documentation) ou o diagrama de sequência em [`DocumentacaoProjeto/Diagramas/scr/diagrama_sequencia.md`](https://github.com/Mendes1801/sipLog/tree/main/DocumentacaoProjeto/Diagramas/scr/diagrama_sequencia.md).
