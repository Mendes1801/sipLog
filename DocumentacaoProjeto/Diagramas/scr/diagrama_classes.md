# Diagrama de Classes - SIP (Core API)

Este diagrama representa o modelo de domínio do back-end, focado no banco de dados relacional PostgreSQL da Core API.

```plantuml
@startuml
skinparam classAttributeIconSize 0
skinparam monochrome true
skinparam shadowing false

package "entity" {

    class Usuario {
        - id: UUID
        - keycloakId: String
        - nome: String
        - bio: String
        - fotoUrl: String
        - dataCadastro: LocalDateTime
    }

    class Bebida {
        - id: UUID
        - nome: String
        - tipo: String
        - fabricante: String
        - caracteristicas: Map<String, String> <<jsonb>>
        - abv: Double
    }

    class Experiencia {
        - id: UUID
        - nota: Double
        - comentario: String
        - fotoPostUrl: String
        - dataCriacao: LocalDateTime
        - quantidadeCurtidas: Integer <<@Formula>>
        - quantidadeComentarios: Integer <<@Formula>>
    }

    class Seguidor {
        - id: UUID
        - dataSeguimento: LocalDateTime
    }

    class Curtida {
        - id: UUID
        - dataCurtida: LocalDateTime
    }

    class Comentario {
        - id: UUID
        - texto: String
        - dataComentario: LocalDateTime
    }

    ' Relacionamentos
    Usuario "1" -- "0..*" Experiencia : registra >
    Bebida "1" -- "0..*" Experiencia : avaliada em >
    
    Usuario "1" -- "0..*" Seguidor : seguidor >
    Usuario "1" -- "0..*" Seguidor : seguido >
    
    Usuario "1" -- "0..*" Curtida : da >
    Experiencia "1" -- "0..*" Curtida : recebe >
    
    Usuario "1" -- "0..*" Comentario : escreve >
    Experiencia "1" -- "0..*" Comentario : contem >
}

note right of Bebida::caracteristicas
  Armazenado como JSONB no 
  PostgreSQL para suportar
  atributos dinâmicos (IBU, Safra, etc)
end note

@enduml
```

# Diagrama de Classes - Core API (Controllers e Services)

Exibe as camadas lógicas da Core API, demonstrando como os Controllers delegam as regras de negócio aos Services, que por sua vez acessam os Repositórios.

```plantuml
@startuml
skinparam classAttributeIconSize 0
skinparam monochrome true

package "br.mackenzie.sipLog.apiCore" {

    package "controller" {
        class ExperienciaCoreController {
            + criarExperiencia(dto: NovaExperienciaDTO)
            + buscarPorId(id: String)
        }
        class FeedCoreController {
            + getFeedGlobal(pageable: Pageable)
            + getFeedAmigos(pageable: Pageable)
            + getFeedMe(pageable: Pageable)
        }
        class UsuariosCoreController {
            + sincronizarUsuario(token: Jwt)
            + seguirUsuario(dto: SeguirAmigoDTO)
        }
    }

    package "service" {
        class ExperienciaService {
            + salvarNovaExperiencia()
            + validarAcesso()
        }
        class FeedService {
            + gerarFeedGlobal()
            + gerarFeedAmigos(usuarioId: String)
        }
        class UsuarioService {
            + criarOuAtualizarUsuario()
            + processarNovoSeguidor()
        }
    }

    package "repository (JPA)" {
        interface ExperienciaRepository {
            + findAllByUsuarioIn()
        }
        interface UsuarioRepository
        interface SeguidorRepository
        interface CurtidaRepository {
            + findExperienciasCurtidasPeloUsuario()
        }
    }

    ExperienciaCoreController --> ExperienciaService
    FeedCoreController --> FeedService
    UsuariosCoreController --> UsuarioService

    ExperienciaService --> ExperienciaRepository
    FeedService --> ExperienciaRepository
    FeedService --> CurtidaRepository : "otimização de curtidas"
    UsuarioService --> UsuarioRepository
    UsuarioService --> SeguidorRepository
}
@enduml
```

# Diagrama de Classes - SipLog BFF

Este diagrama apresenta a estrutura interna do BFF, responsável por receber requisições do App, interceptar os tokens JWT do Keycloak e formatar as respostas para a interface mobile.

```plantuml
@startuml
skinparam classAttributeIconSize 0
skinparam monochrome true

package "br.mackenzie.sipLogBFF" {

    package "config" {
        class RestClientConfig {
            + restClient(builder: RestClient.Builder, interceptor: TokenRelayInterceptor): RestClient
        }

        class TokenRelayInterceptor {
            + intercept(request: HttpRequest, body: byte[], execution: ClientHttpRequestExecution): ClientHttpResponse
            - extractToken(): String
        }
    }

    package "controller" {
        class SipBffController {
            - coreApiUrl: String
            - restClient: RestClient
            + getGlobalFeed(page: int, size: int): ResponseEntity<Page<FeedResponseDTO>>
            + getAmigosFeed(page: int, size: int): ResponseEntity<Page<FeedResponseDTO>>
            + getDetalheBebida(id: String): ResponseEntity<DetalheBebidaDTO>
            + postExperiencia(imagem: MultipartFile, dto: NovoRegistroRequestDTO): ResponseEntity<RegistroDTO>
            + seguirUsuario(amigoId: String): ResponseEntity<Void>
            - calcularTempoDecorrido(data: LocalDateTime): String
        }
    }

    package "dto.response" {
        class FeedResponseDTO {
            + idExperiencia: String
            + nomeUsuario: String
            + fotoUsuario: String
            + fotoPost: String
            + nota: Double
            + tempoDecorrido: String
            + totalCurtidas: Integer
            + totalComentarios: Integer
            + usuarioJaCurtiu: Boolean
        }
        
        class PerfilResponseDTO
        class ExploreResponseDTO
    }

    SipBffController --> RestClientConfig : utiliza RestClient
    RestClientConfig --> TokenRelayInterceptor : registra na cadeia HTTP
    SipBffController ..> FeedResponseDTO : formata e retorna
}
@enduml
```