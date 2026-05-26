# 📚 DocumentacaoProjeto — Documentação Técnica do SipLog

Central de toda a documentação técnica, contratos de API, diagramas arquiteturais e materiais de design UX/UI do projeto SipLog.

---

## Sumário

1. [Estrutura do Diretório](#estrutura-do-diretório)
2. [Diagramas UML e Arquiteturais](#diagramas-uml-e-arquiteturais)
3. [Contratos de API (OpenAPI)](#contratos-de-api-openapi)
4. [Design Visual UX/UI](#design-visual-uxui)
5. [Como Visualizar os Diagramas](#como-visualizar-os-diagramas)
6. [Histórico de Versões da API](#histórico-de-versões-da-api)

---

## Estrutura do Diretório

```
DocumentacaoProjeto/
│
├── Diagramas/
│   └── scr/
│       ├── diagrama_classes.md      ← Diagrama de classes UML (entidades e relacionamentos)
│       ├── diagrama_atividades.md   ← Fluxos de atividades de negócio
│       ├── diagrama_componentes.md  ← Visão de componentes e microsserviços
│       └── diagrama_sequencia.md   ← Sequência de chamadas entre sistemas
│
├── ContratosEndPoint/
│   └── BFF/
│       ├── api-docsV2.json          ← Versão 2 do contrato OpenAPI
│       ├── api-docsV6.json          ← Versão 6 do contrato OpenAPI
│       ├── api-docsV7.json          ← Versão 7 do contrato OpenAPI
│       └── api-docsV8.json          ← Versão 8 do contrato OpenAPI (mais recente aqui)
│
├── BASE_Visualização.png            ← Mockup base das telas do app
├── Base PNG.png                     ← Versão exportada do design base
├── Base.psd                         ← Arquivo editável Photoshop (design das telas)
├── BT_AdcFoto.png                   ← Asset: botão adicionar foto
├── BT_Amigos.png                    ← Asset: botão amigos
├── BT_Mapa.png                      ← Asset: botão mapa
├── BT_Notificação.png               ← Asset: botão notificações
├── BT_Pesquisar.png                 ← Asset: botão pesquisar
├── BT_Profile.png                   ← Asset: botão perfil
├── BT_Vinho.png                     ← Asset: botão vinho
└── bakso_sapi.zip                   ← Arquivo da fonte customizada BaksoSapi
```

---

## Diagramas UML e Arquiteturais

Os diagramas estão em formato **Mermaid** e **PlantUML**, renderizáveis diretamente no GitHub.

### 📐 Diagrama de Classes (`diagrama_classes.md`)

Representa as entidades do domínio e seus relacionamentos:

- `Usuario` — `Experiencia` (1:N)
- `Experiencia` — `Bebida` (N:1)
- `Experiencia` — `Curtida` (1:N)
- `Experiencia` — `Comentario` (1:N)
- `Usuario` — `Seguidor` — `Usuario` (N:N via tabela associativa)
- `Usuario` — `Notificacao` (1:N)

**Destaques do modelo:**
- `Bebida.caracteristicas` é JSONB (atributos dinâmicos por categoria)
- `Experiencia.totalCurtidas` e `totalComentarios` são campos calculados via `@Formula` (Hibernate)
- `Notificacao.tipo` é um enum: `CURTIDA`, `COMENTARIO`, `NOVO_SEGUIDOR`
- `Experiencia.visibilidade` é um enum: `PUBLICA`, `AMIGOS`, `PRIVADA`

### 🔄 Diagrama de Atividades (`diagrama_atividades.md`)

Modela os fluxos de negócio principais:

- Fluxo de login e sincronização do usuário
- Fluxo de criação de uma nova experiência (com upload de foto)
- Fluxo de curtida e geração de notificação
- Fluxo de seguir usuário

### 🧩 Diagrama de Componentes (`diagrama_componentes.md`)

Visão de alto nível dos microsserviços e suas conexões (PlantUML):

```
Dispositivo Mobile (Flutter)
        │ REST + JWT
        ▼
SipLog BFF (Spring Boot) ──────────────► AWS S3
        │ REST interno + TokenRelay
        ▼
SipLog Core API (Spring Boot) ─────────► PostgreSQL
        │
        ▼
Keycloak (IAM) ◄──── (validação JWT)
```

### 📋 Diagrama de Sequência (`diagrama_sequencia.md`)

Sequência detalhada das chamadas entre sistemas para os fluxos principais:

1. **Login:** `Flutter → Keycloak → JWT → Flutter → BFF → Core API (/sync)`
2. **Criar experiência:** `Flutter → BFF → S3 (upload foto) + Core API (persistir)`
3. **Curtir post:** `Flutter → BFF → Core API → NotificacaoEvent → Notificacao`
4. **Carregar feed:** `Flutter → BFF → Core API → BFF (formata) → Flutter`

---

## Contratos de API (OpenAPI)

Os contratos formalizam a comunicação entre o **app Flutter** e o **BFF**. Seguem o padrão **OpenAPI 3.0 (Swagger)**.

### Versão mais recente: `api-docsV8.json`

Endpoints documentados:

| Grupo | Endpoints |
|---|---|
| **Usuários** | `GET/PUT/DELETE /api/v1/usuarios/me`, `POST /sync`, `GET /{id}`, `GET /{id}/seguidores`, `POST /{id}/seguir` |
| **Feed** | `GET /api/v1/feed/me`, `/global`, `/amigos`, `/usuarios/{id}` |
| **Experiências** | `POST /api/v1/experiencias`, `PUT/DELETE /{id}`, `POST /{id}/curtir` |
| **Comentários** | `GET/POST /api/v1/experiencias/{id}/comentarios`, `PUT/DELETE /…/{idComentario}` |
| **Bebidas** | `GET /api/v1/bebidas/buscar`, `GET /{id}`, `POST /api/v1/bebidas` |
| **Upload** | `POST /api/v1/upload` |
| **Notificações** | `GET /api/v1/notificacoes`, `GET /nao-lidas/count`, `PATCH /{id}/lida` |

### Como usar os contratos

**Opção 1 — Swagger UI (ambiente local):**
```
http://localhost:8081/swagger-ui/index.html
```

**Opção 2 — Importar no Postman:**
1. Abra o Postman
2. `Import` → escolha o arquivo `api-docsV8.json`
3. Uma collection completa será criada automaticamente

**Opção 3 — Swagger Editor online:**
1. Acesse [editor.swagger.io](https://editor.swagger.io)
2. `File` → `Import File` → selecione `api-docsV8.json`

---

## Design Visual UX/UI

### Arquivos de Design

| Arquivo | Descrição |
|---|---|
| `Base.psd` | Arquivo Photoshop editável com o layout completo das telas |
| `Base PNG.png` | Exportação em PNG da tela base (referência para o Flutter) |
| `BASE_Visualização.png` | Mockup de visualização com componentes anotados |
| `BT_*.png` | Recortes individuais dos botões da barra de navegação |

### Botões da Navegação

Os assets `BT_*.png` correspondem aos botões da barra de navegação inferior do app:

| Asset | Botão | Tela Correspondente |
|---|---|---|
| `BT_Vinho.png` | Vinho 🍷 | Feed Global |
| `BT_Amigos.png` | Amigos 👥 | Feed de Amigos |
| `BT_AdcFoto.png` | Adicionar Foto 📷 | Nova Experiência |
| `BT_Pesquisar.png` | Buscar 🔍 | Busca de Usuários |
| `BT_Profile.png` | Perfil 👤 | Meu Perfil |
| `BT_Mapa.png` | Mapa 📍 | Localização no Feed |
| `BT_Notificação.png` | Notificações 🔔 | Notificações |

### Fonte Customizada

A fonte **BaksoSapi** (OTF) foi utilizada no logotipo e elementos de marca do app. O arquivo `bakso_sapi.zip` contém os arquivos da fonte. O `BaksoSapi.otf` já está disponível em `mobile/front/sip_log_mobile/assets/fonts/`.

---

## Como Visualizar os Diagramas

### Mermaid (GitHub)

Os arquivos `.md` na pasta `Diagramas/scr/` são renderizados automaticamente pelo GitHub quando acessados pelo browser. Basta navegar até o arquivo no repositório.

### PlantUML

Para renderizar diagramas PlantUML localmente:

```bash
# Via Docker
docker run --rm -v $(pwd):/data plantuml/plantuml -tpng /data/diagrama_componentes.md
```

Ou use a extensão **PlantUML** no VS Code (requer Java).

### Visualizador online

- Mermaid: [mermaid.live](https://mermaid.live)
- PlantUML: [plantuml.com/plantuml](https://www.plantuml.com/plantuml)

---

## Histórico de Versões da API

| Versão | Arquivo | Mudanças principais |
|---|---|---|
| V2 | `api-docsV2.json` | Versão inicial: endpoints básicos de usuário e experiência |
| V6 | `api-docsV6.json` | Adição de feed paginado, curtidas e comentários |
| V7 | `api-docsV7.json` | Adição de notificações e contagem de não lidas |
| V8 | `api-docsV8.json` | Adição de upload de mídia, busca de usuários e seguir/deixar de seguir |

> Versões mais recentes (`V9`, `V10`, `V11`) estão disponíveis em `mobile/front/sip_log_mobile/API_Contracts/` — usadas pelo time mobile durante o desenvolvimento.

---

> Para dúvidas sobre os contratos de API, consulte o Swagger UI do ambiente local ou abra uma issue no repositório.
