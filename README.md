# 🍷 SipLog

Bem-vindo ao repositório do **SipLog**, uma plataforma social focada no registro, compartilhamento e descoberta de experiências com bebidas (como vinhos, cervejas e outros). O objetivo é conectar pessoas através de suas degustações, permitindo o engajamento com amigos na rede.

## 📋 Sumário
1. [Funcionalidades](#-funcionalidades)
2. [Arquitetura](#-arquitetura)
3. [Modelo de Dados](#-modelo-de-dados)
4. [Stack Tecnológica](#-stack-tecnológica)
5. [Pré-requisitos](#-pré-requisitos)
6. [Como Executar e Build Local](#-como-executar-e-build-local)
7. [Como Rodar o Front (Mobile)](#-como-rodar-o-front-mobile)
8. [Contratos de API](#-contratos-de-api)

## 🚀 Funcionalidades
- **Autenticação e Gestão de Perfil:** Cadastro de usuários e login seguro (via Keycloak), com suporte a avatares e biografia.
- **Registro de Experiências:** Adicione bebidas consumidas, avaliações (rating), comentários e fotos (armazenadas em Nuvem).
- **Feed Social:** Visualize as atividades e experiências dos amigos em tempo real.
- **Interação:** Curtidas e comentários nas experiências postadas por outros usuários.
- **Rede de Amigos:** Sistema de seguir e ser seguido, busca por usuários e visualização de perfis.
- **Notificações:** Alertas sobre novas curtidas, comentários e novos seguidores.
- **Integração Externa:** Consulta de catálogo de vinhos.

## 🏛️ Arquitetura
O sistema segue uma arquitetura orientada a microsserviços com o padrão **BFF (Backend for Frontend)**:
- **Mobile (Frontend):** Aplicativo Flutter responsável pela interface e interação com o usuário, desenhado com foco em heurísticas de usabilidade (UX/UI).
- **sipLog-BFF:** Serviço Spring Boot que atua como porta de entrada. Intercepta requisições, faz a validação de segurança (Token Relay com Keycloak), gerencia o upload de mídias (AWS S3) e agrega dados antes de enviá-los ao mobile.
- **Core API:** Serviço Spring Boot responsável pelo domínio principal da aplicação, regras de negócio e persistência de dados no banco relacional.
- **Infraestrutura:** Keycloak para Identity & Access Management (IAM) e PostgreSQL para persistência, orquestrados via Docker.

## 📊 Modelo de Dados
Os principais domínios do sistema incluem:
- **Usuario:** Entidade central com dados de perfil e credenciais.
- **Bebida:** Catálogo de bebidas registradas.
- **Experiencia:** O elo principal, relacionando um Usuário a uma Bebida, contendo nota, texto e URL da foto.
- **Comentario / Curtida:** Entidades de engajamento vinculadas a uma Experiência.
- **Notificacao:** Registro de eventos relevantes para alertar o usuário.
- **Seguidor:** Tabela de relacionamento (N:N) gerenciando a rede de amigos.

## 💻 Stack Tecnológica
- **Frontend:** Flutter, Dart
- **Backend:** Java 17, Spring Boot, Spring Security, Maven
- **Identity Provider:** Keycloak
- **Storage:** AWS S3 (Mídias)
- **Banco de Dados:** PostgreSQL
- **Infra & DevOps:** Docker, Docker Compose, Jenkins

## ⚙️ Pré-requisitos
Antes de começar, certifique-se de ter instalado em sua máquina:
- [Java 17 (JDK)](https://adoptium.net/)
- [Flutter SDK](https://flutter.dev/docs/get-started/install)
- [Docker e Docker Compose](https://www.docker.com/)
- [Git](https://git-scm.com/)

---

## 🛠️ Como Executar e Build Local

A execução completa do projeto depende de subir a infraestrutura base e, em seguida, as aplicações Java.

### 1. Subindo a Infraestrutura (Banco de Dados e Keycloak)
Na raiz do projeto, navegue até a pasta de infraestrutura e execute o Docker Compose:
```bash
cd infra/localRun
docker-compose -f docker-compose-local.yml up -d
```
*Isso iniciará os containers do PostgreSQL e do Keycloak com as configurações de desenvolvimento.*

### 2. Executando a Core API
Abra um novo terminal e inicie o serviço de Core:
```bash
cd core_api/apiCore-sipLog
./mvnw spring-boot:run
```
*A API Core rodará na porta designada (ex: 8081).*

### 3. Executando o BFF (Backend for Frontend)
Configure suas variáveis de ambiente copiando o arquivo de exemplo (se necessário) para inserir credenciais da AWS S3 e detalhes do Keycloak.
Em um novo terminal, inicie o BFF:
```bash
cd sipLog-BFF
./mvnw spring-boot:run
```
*O BFF iniciará (ex: porta 8080) e fará a ponte entre o Flutter e a Core API.*

---

## 📱 Como Rodar o Front (Mobile)

O aplicativo mobile é construído com Flutter e se comunica diretamente com o `sipLog-BFF`.

1. Navegue até o diretório do frontend:
```bash
cd mobile/front/sip_log_mobile
```
2. Baixe as dependências do Dart/Flutter:
```bash
flutter pub get
```
3. Execute o projeto no emulador (Android/iOS) ou dispositivo físico:
```bash
flutter run
```

---

## 📄 Contratos de API
Os contratos de integração entre o Frontend (Mobile) e o BFF estão documentados e versionados (JSON Swagger/OpenAPI). Para entender as rotas, payloads e respostas, consulte os arquivos presentes em:
👉 `DocumentacaoProjeto/ContratosEndPoint/BFF/`

Para mais detalhes visuais, diagramas estruturais e de atividades, acesse a pasta [DocumentacaoProjeto](./DocumentacaoProjeto/README.md).
