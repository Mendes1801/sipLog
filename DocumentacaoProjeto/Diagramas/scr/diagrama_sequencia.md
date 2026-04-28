# Diagrama de Sequência: Postar Nova Experiência com Imagem

Fluxo detalhado demonstrando o App comunicando com o BFF, que orquestra o upload da imagem e encaminha os metadados para a Core API.

```plantuml
@startuml
actor "Usuário (App)" as User
participant "Flutter App" as App
participant "SipLog BFF" as BFF
participant "AWS S3" as S3
participant "Core API" as Core
database "PostgreSQL" as DB

User -> App: Preenche form da Experiência\n(Bebida, Nota, Comentário, Foto)
activate App

App -> BFF: POST /api/v1/experiencias\n(MultipartFile, JSON DTO, Bearer Token)
activate BFF

BFF -> BFF: Valida Dados Recebidos

alt Foto Presente na Requisição
    BFF -> S3: Faz upload do MultipartFile
    activate S3
    S3 --> BFF: Retorna URL Gerada (fotoPostUrl)
    deactivate S3
end

BFF -> BFF: Monta NovaExperienciaDTO \n(Insere a URL da Imagem)

BFF -> Core: POST /apiCore/v1/experiencias\n(NovaExperienciaDTO, TokenRelay)
activate Core

Core -> Core: Extrai userId do KeycloakToken
Core -> DB: Busca Usuario e Bebida
activate DB
DB --> Core: Entidades Encontradas
deactivate DB

Core -> DB: save(Experiencia)
activate DB
DB --> Core: Experiencia Salva (com UUID)
deactivate DB

Core --> BFF: Retorna HTTP 201 (ReturnNovaExperienciaDTO)
deactivate Core

BFF -> BFF: Transforma e traduz resposta (se necessário)
BFF --> App: Retorna HTTP 201 Created (RegistroDTO)
deactivate BFF

App --> User: Exibe feedback visual de sucesso
deactivate App
@enduml
```

# Diagrama de Sequência: Carregamento do Feed Global

Fluxo mostrando como o Flutter solicita o feed, como o BFF repassa a requisição e como a Core API utiliza as otimizações do banco de dados (JPA/Hibernate) para devolver os dados.

```plantuml
@startuml
actor "Flutter App" as App
participant "SipBffController" as BFF
participant "FeedCoreController" as CoreCtrl
participant "FeedService" as CoreSvc
database "PostgreSQL" as DB

App -> BFF: GET /feed/global?page=0&size=10
activate BFF

BFF -> CoreCtrl: GET /internal/v1/feed/global?page=0\n(Headers: Bearer Token)
activate CoreCtrl

CoreCtrl -> CoreSvc: getFeedGlobal(pageable)
activate CoreSvc

CoreSvc -> DB: findAll(pageable) \n(usando @EntityGraph para evitar N+1)
activate DB
DB --> CoreSvc: Page<Experiencia> (Postagens)
deactivate DB

note right of CoreSvc
  Otimização: Busca em lote quais posts
  desta página o usuário logado já curtiu
end note

CoreSvc -> DB: findExperienciasCurtidasPeloUsuario(listaIds, userId)
activate DB
DB --> CoreSvc: List<UUID> (IDs já curtidos)
deactivate DB

CoreSvc -> CoreSvc: Mapeia Experiencias para FeedItemDTO\n(incluindo flag 'usuarioJaCurtiu')
CoreSvc --> CoreCtrl: Page<FeedItemDTO>
deactivate CoreSvc

CoreCtrl --> BFF: HTTP 200 OK (JSON bruto com LocalDateTime)
deactivate CoreCtrl

BFF -> BFF: Itera sobre a Página
BFF -> BFF: Converte LocalDateTime para "tempoDecorrido" (ex: "Há 3h")
BFF -> BFF: Instancia Page<FeedResponseDTO>

BFF --> App: HTTP 200 OK (JSON formatado para UI)
deactivate BFF
@enduml
```

# Diagrama de Sequência: Ação de Seguir Usuário

Demonstra o fluxo da rede social onde o Usuário A (Logado) decide seguir o Usuário B (Amigo).

```plantuml
@startuml
actor "Usuário A" as User
participant "Flutter App" as App
participant "BFF" as BFF
participant "Core API" as Core
database "PostgreSQL" as DB

User -> App: Toca no botão "Seguir" (ID: Usuário B)
activate App

App -> BFF: POST /usuarios/B/seguir
activate BFF

BFF -> Core: POST /apiCore/v1/usuarios/seguir\n(SeguirAmigoDTO, Token)
activate Core

Core -> Core: Extrai ID (Usuário A) do JWT
Core -> DB: Busca Entidade Usuario A
Core -> DB: Busca Entidade Usuario B

alt Se Usuário B não existir
    Core --> BFF: HTTP 404 (RecursoNaoEncontradoException)
    BFF --> App: HTTP 404
else Validação OK
    Core -> DB: Verifica se já segue
    alt Já segue
        Core --> BFF: HTTP 400 (Bad Request - Já é seguidor)
    else Não segue
        Core -> DB: Cria registro na tabela 'Seguidor'
        activate DB
        DB --> Core: Seguidor Salvo
        deactivate DB
        Core --> BFF: HTTP 201 Created
    end
end

deactivate Core
BFF --> App: HTTP 201 Created
deactivate BFF

App --> User: Atualiza botão para "Seguindo"
deactivate App
@enduml
```