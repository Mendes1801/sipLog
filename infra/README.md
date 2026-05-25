# 🐳 Infraestrutura SipLog

Este diretório contém todos os manifestos e arquivos necessários para provisionar a infraestrutura de apoio do sistema, tanto para ambiente de desenvolvimento local quanto para produção/CI.

## Estrutura
- `localRun/`: Arquivos para subir o ambiente localmente. Contém o `docker-compose-local.yml` que orquestra o PostgreSQL e o Keycloak.
- `prodRun/`: Arquiteturas escaláveis e orquestração de produção (`docker-compose-prod.yml`, `docker-compose-bds.yml`, `docker-compose-spring.yml`).
- `Jenkinsfile`: Pipeline as Code para automatização de CI/CD (Continuous Integration / Continuous Deployment).

## Como subir o ambiente de Desenvolvimento
Para não precisar instalar banco de dados localmente, utilize o Docker:

1. Copie o arquivo `.env.exemple` para um novo arquivo chamado `.env` e preencha com suas variáveis locais.
2. Na pasta `localRun`:
   ```bash
   docker-compose -f docker-compose-local.yml up -d
   ```
3. O Keycloak estará disponível na porta definida (geralmente 8080 ou 8082) e o banco de dados Postgres na 5432. Acesse o painel de administração do Keycloak para configurar os *Realms* e *Clients* necessários para o BFF e o Flutter.
