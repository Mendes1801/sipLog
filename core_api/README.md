# ⚙️ Core API - SipLog

O **Core API** é o microsserviço principal responsável por toda a regra de negócio central, domínio e persistência de dados do SipLog.

## Responsabilidades
- Gerenciamento completo de CRUD das entidades principais (Usuários, Bebidas, Experiências, Curtidas, Comentários, Notificações e Seguidores).
- Conexão e transações com o banco de dados PostgreSQL.
- Exposição de APIs REST internas para serem consumidas exclusivamente pelo `sipLog-BFF`.
- Emissão de eventos de sistema (ex: `NotificacaoEvent` ao receber uma curtida).

## Estrutura do Projeto
Desenvolvido em **Java 17** e **Spring Boot**, o projeto segue a separação clássica de camadas MVC:
- `controller/`: Endpoints REST isolados por domínio.
- `service/`: Regras de negócio e processamento de eventos.
- `repository/`: Interfaces Spring Data JPA para acesso ao banco.
- `entity/`: Mapeamento ORM (JPA/Hibernate) das tabelas.
- `dto/`: Objetos de transferência de dados divididos entre inputs (`dtoPost`, `dtoPut`) e outputs (`dtoGet`).

## Como Executar (Local)
1. Certifique-se de que o banco de dados está rodando via Docker (veja a pasta `/infra`).
2. Execute o wrapper do Maven:
   ```bash
   cd apiCore-sipLog
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
