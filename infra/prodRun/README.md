# 🚀 prodRun — Deploy de Produção

Configurações Docker Compose e pipeline CI/CD para o ambiente de produção do SipLog, voltado para implantação em instâncias cloud (AWS EC2 ou similar).

---

## Sumário

1. [Arquivos desta Pasta](#arquivos-desta-pasta)
2. [Pré-requisitos na VM](#pré-requisitos-na-vm)
3. [Subir o Ambiente pela Primeira Vez](#subir-o-ambiente-pela-primeira-vez)
4. [Atualizar Apenas os Serviços Spring](#atualizar-apenas-os-serviços-spring)
5. [Estratégia de Arquivos Compose](#estratégia-de-arquivos-compose)
6. [Pipeline Jenkins](#pipeline-jenkins)

---

## Arquivos desta Pasta

| Arquivo | Responsabilidade |
|---|---|
| `docker-compose-bds.yml` | Sobe o banco de dados PostgreSQL do SipLog |
| `docker-compose-keycloak.yml` | Sobe o Keycloak com banco dedicado |
| `docker-compose-spring.yml` | Sobe Core API e BFF (com build das imagens) |
| `docker-compose-prod.yml` | Stack monolítica alternativa (todos os serviços em um único compose) |
| `Jenkinsfile` | Pipeline declarativo CI/CD |

---

## Pré-requisitos na VM

### 1. Instalar Docker e Docker Compose v2

```bash
sudo apt update && sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable docker && sudo systemctl start docker
sudo usermod -aG docker $USER
```

### 2. Criar a rede Docker compartilhada

**Obrigatório antes de subir qualquer serviço:**

```bash
sudo docker network create sip-network
```

Todos os containers `prodRun` utilizam essa rede para comunicação interna.

### 3. Configurar variáveis de ambiente

Copie e edite o `.env` na raiz de `infra/`:

```bash
cp infra/.env.exemple infra/.env
# Preencha com os valores de produção
```

---

## Subir o Ambiente pela Primeira Vez

Execute a partir do diretório `infra/prodRun/`:

```bash
cd infra/prodRun

sudo docker compose \
  -f docker-compose-bds.yml \
  -f docker-compose-keycloak.yml \
  -f docker-compose-spring.yml \
  up -d
```

**Ordem de inicialização:**
1. PostgreSQL (app + Keycloak) ficam prontos primeiro
2. Keycloak sobe e conecta ao seu banco
3. Core API aguarda o PostgreSQL
4. BFF aguarda a Core API e o Keycloak

> Após o primeiro boot, acesse o Keycloak em `http://<IP_PUBLICO>:8080` para configurar o Realm `BFF` e o Client `sipLog`.

---

## Atualizar Apenas os Serviços Spring

Após um novo deploy via Jenkins (novos JARs ou imagens Docker geradas):

```bash
cd infra/prodRun
sudo docker compose -f docker-compose-spring.yml up -d --build
```

Isso reconstrói e reinicia apenas a `core-api` e o `bff`, sem tocar nos bancos de dados ou no Keycloak.

---

## Estratégia de Arquivos Compose

A separação em múltiplos arquivos permite:

| Arquivo | Quando re-executar |
|---|---|
| `docker-compose-bds.yml` | Raramente — apenas em migração de banco ou primeiro setup |
| `docker-compose-keycloak.yml` | Raramente — apenas em atualização de versão do Keycloak |
| `docker-compose-spring.yml` | **A cada deploy** — contém os serviços Java |

Isso minimiza o risco de operações de deploy afetarem dados persistidos.

---

## Pipeline Jenkins

O `Jenkinsfile` automatiza o ciclo completo de CI/CD:

| Etapa | Comando |
|---|---|
| **Checkout** | `git clone` do branch `main` |
| **Build** | `mvn clean compile` em Core API e BFF |
| **Testes** | `mvn test` + publicação de relatórios JUnit |
| **SonarQube** | Análise estática com Quality Gate obrigatório |
| **Package** | `mvn package` → gera JARs |
| **Docker Build** | `docker build` das imagens `core-api` e `bff` |
| **Push Registry** | Envia imagens ao Docker Registry configurado |
| **Deploy** | SSH na VM → `docker compose pull` + `up --force-recreate` |

### Ferramentas Jenkins necessárias

- **Maven3** — instalado via Global Tool Configuration
- **JDK21** — instalado via Global Tool Configuration
- **SonarQube Server** — configurado em Manage Jenkins → Configure System
- **SSH credentials** — para acesso à VM de produção

---

> Para o ambiente de desenvolvimento local, consulte [`../localRun/`](https://github.com/Mendes1801/sipLog/tree/main/infra/localRun) e [`../README.md`](https://github.com/Mendes1801/sipLog/tree/main/infra).
