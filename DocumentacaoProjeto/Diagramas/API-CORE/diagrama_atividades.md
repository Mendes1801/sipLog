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