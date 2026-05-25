# 📚 Documentação do Projeto SipLog

Central de informações, contratos técnicos e materiais de design UX/UI da plataforma SipLog.

## Conteúdo

### 1. Diagramas (`/Diagramas/scr`)
Possui arquivos Markdown/Mermaid contendo as representações arquiteturais:
- `diagrama_classes.md`: Modelo de classes e relacionamentos (Entities).
- `diagrama_atividades.md`: Fluxos lógicos de negócio.
- `diagrama_componentes.md`: Visão dos microsserviços.
- `diagrama_sequencia.md`: Ordem de chamadas (ex: Login Flutter -> BFF -> Keycloak).

### 2. Contratos de API (`/ContratosEndPoint/BFF`)
Os contratos que formalizam a comunicação HTTP entre o Mobile e o BFF. As definições seguem o padrão OpenAPI/Swagger (arquivos `.json` de versão, como `api-docsV8.json`).

### 3. Design Visual (UX/UI)
Arquivos base de telas, protótipos em `.psd` (Photoshop) e recortes `.png` (Botões de Perfil, Mapa, Amigos, Vinho) que orientaram o desenvolvimento das telas no Flutter. Também inclui fontes tipográficas customizadas (ex: *BaksoSapi.otf*).
