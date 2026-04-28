# Diagrama de Atividades: Autenticação e Sincronização (Primeiro Login)

Fluxo demonstrando como a arquitetura lida com usuários recém-registrados no Keycloak sendo sincronizados na base do SipLog (Core API).

```plantuml
@startuml
skinparam ConditionEndStyle hline

start
:Abrir aplicativo SipLog;
:Usuário solicita Login;

if (Possui Token Válido?) then (Sim)
    :Acessar Feed;
else (Não)
    :Redirecionar para Tela do Keycloak;
    :Usuário insere Credenciais;
    if (Credenciais Válidas?) then (Não)
        :Exibir Erro de Autenticação;
        stop
    else (Sim)
        :Keycloak retorna JWT (Access Token);
        :App envia requisição inicial para o BFF\n(ex: GET /perfil/me);
        
        partition "SipLog BFF / Core API" {
            :Interceptar Token JWT;
            :Extrair keycloakId (sub) e E-mail/Nome;
            :Consultar Usuário no Banco (Core API);
            
            if (Usuário Existe?) then (Sim)
                :Retornar Perfil do Usuário;
            else (Não - Primeiro Acesso)
                :Instanciar nova entidade Usuario;
                :Atribuir keycloakId e dados extraídos;
                :Salvar no PostgreSQL (tb_usuario);
                :Retornar Perfil (Novo Usuário);
            endif
        }
        :App armazena os dados do usuário atual;
        :Renderizar Tela Inicial (Dashboard/Feed);
    endif
endif

stop
@enduml
```

# Diagrama de Atividades: Geração do Feed na Core API

Detalha o processo interno e de otimização de banco de dados (`FeedService`) executado pela Core API antes de devolver as postagens.

```plantuml
@startuml
skinparam ConditionEndStyle hline

start
:Receber requisição do Feed (Global ou Amigos);
:Validar Autenticação (JWT);

if (Tipo de Feed?) then (Feed de Amigos)
    :Consultar tb_seguidor para extrair lista \nde IDs que o usuário segue;
    :Executar Query paginada na tb_experiencia \nWHERE usuario_id IN (Lista Amigos);
else (Feed Global)
    :Executar Query paginada na tb_experiencia \nORDER BY dataCriacao DESC;
endif

:Obter Página de Postagens (Page<Experiencia>);
:Extrair lista de IDs das postagens recuperadas;

if (Página está vazia?) then (Sim)
    :Retornar Página Vazia;
    stop
else (Não)
    :Consultar tb_curtida verificando se o usuário \nlogado curtiu algum dos IDs extraídos;
    :Obter Set<UUID> de postagens já curtidas;
    
    :Iniciar Mapeamento para DTO;
    while (Existem postagens na página?) is (Sim)
      :Converter Entidade Experiencia para DTO;
      if (ID da postagem está no Set de curtidas?) then (Sim)
          :Definir dto.usuarioJaCurtiu = true;
      else (Não)
          :Definir dto.usuarioJaCurtiu = false;
      endif
      :Adicionar à lista final;
    endwhile (Não)
    
    :Retornar Page<FeedItemDTO> para o BFF;
endif

stop
@enduml
```