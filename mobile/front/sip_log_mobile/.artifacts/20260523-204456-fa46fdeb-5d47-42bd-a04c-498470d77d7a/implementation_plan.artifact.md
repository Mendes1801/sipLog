# Plano de Implementação - Novas Funcionalidades e Correções

Este plano detalha as correções na tela de "Nova Experiência" e a criação das telas de registro de bebidas e busca de usuários.

## User Review Required

- **Rota de Busca de Usuários**: O contrato da API não mostra explicitamente uma rota `/api/v1/usuarios/buscar`. Vou implementar a tela de busca assumindo que essa rota existe (seguindo o padrão de `bebidas/buscar`) ou usarei a lista de seguidores/feed global como fallback. **Confirme se a rota `/api/v1/usuarios/buscar?q=...` está disponível.**
- **Características da Bebida**: Para o registro de novas bebidas, implementarei um campo dinâmico de chave/valor para as características.

## Proposed Changes

### Fixes em `Nova Experiência`

#### [nova_experiencia_screen.dart](file:///C:/Users/brasi/Documents/ProjetoSIP/sipLog/mobile/front/sip_log_mobile/lib/screens/nova_experiencia_screen.dart)
- Substituir `Image.network` por `Image.file` para exibir a miniatura da foto selecionada (usando `dart:io`).
- Corrigir a lógica de carregamento de bebidas:
    - Adicionar um campo de busca ou garantir que a busca com string vazia retorne dados.
    - Adicionar um botão "+" ao lado do dropdown para abrir a tela de registro de nova bebida.

---

### Registro de Bebidas

#### [NEW] [nova_bebida_screen.dart](file:///C:/Users/brasi/Documents/ProjetoSIP/sipLog/mobile/front/sip_log_mobile/lib/screens/nova_bebida_screen.dart)
- Criar formulário com os campos: Nome, Fabricante, Categoria.
- Seção dinâmica para adicionar "Características" (ex: Teor Alcoólico, Safra, etc).
- Integração com `HttpBebidaService.adicionarBebida`.

---

### Busca e Seguimento de Usuários

#### [http_user_service.dart](file:///C:/Users/brasi/Documents/ProjetoSIP/sipLog/mobile/front/sip_log_mobile/lib/services/http_user_service.dart)
- Adicionar método `buscarUsuarios(String query)`.
- Adicionar método `getSeguidores(int idUsuario)`.

#### [NEW] [busca_usuarios_screen.dart](file:///C:/Users/brasi/Documents/ProjetoSIP/sipLog/mobile/front/sip_log_mobile/lib/screens/busca_usuarios_screen.dart)
- Barra de pesquisa para encontrar usuários pelo nome.
- Lista de resultados com botão "Seguir/Parar de Seguir".
- Navegação para o perfil do usuário ao clicar na foto/nome.

---

### Navegação Base

#### [main.dart](file:///C:/Users/brasi/Documents/ProjetoSIP/sipLog/mobile/front/sip_log_mobile/lib/main.dart)
- Substituir o `Center(child: Text('Pesquisa'))` na aba de busca pela `BuscaUsuariosScreen`.

## Verification Plan

### Manual Verification
- **Miniatura de Foto**: Selecionar uma foto na galeria e verificar se a miniatura aparece sem erros.
- **Dropdown de Bebidas**: Abrir a tela de nova experiência e verificar se as bebidas cadastradas no banco aparecem na lista.
- **Registro de Bebida**: Cadastrar uma nova bebida e verificar se ela aparece imediatamente no dropdown da tela anterior.
- **Seguir Usuários**: Buscar por um usuário conhecido, clicar em seguir e verificar se o estado do botão muda e se a estatística de "seguindo" no perfil é atualizada.
