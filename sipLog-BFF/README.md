# 🛡️ SipLog BFF (Backend for Frontend)

O **BFF** atua como a única porta de entrada (API Gateway) para o aplicativo Mobile do SipLog. Ele abstrai a complexidade do backend, agrega chamadas e lida com segurança e integrações externas.

## Responsabilidades
- **Autenticação e Segurança:** Integração direta com o Keycloak via OAuth2/OIDC. O BFF atua como Resource Server, interceptando requisições, validando o Bearer Token e repassando a identidade de forma segura para a Core API (via `TokenRelayInterceptor`).
- **Armazenamento de Mídias (S3):** Processa o upload de avatares e imagens de experiências, enviando os arquivos para o AWS S3 (`StorageBffService`) e salvando apenas a URL na base de dados.
- **Agregação de Dados:** Consome a Core API e formata/padroniza os JSONs para o consumo otimizado do Flutter (ex: formatação de datas, agregação de perfil e feed).

## Estrutura do Projeto
Desenvolvido em **Java 17** e **Spring Boot**, contendo configurações robustas de `RestClientConfig` para chamadas HTTP internas e `SecurityConfig` para validação de JWT.

## Como Executar (Local)
O BFF necessita que o Keycloak e a Core API estejam operacionais.
Além disso, configure as variáveis de ambiente necessárias para a AWS (via `.env` ou export) antes de rodar:
```bash
./mvnw clean install
./mvnw spring-boot:run
```
