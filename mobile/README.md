# 📱 mobile — App Flutter do SipLog

Aplicativo oficial do SipLog desenvolvido com **Flutter**, suportando Android, iOS, Web e Desktop a partir de uma única base de código Dart. A interface segue heurísticas de usabilidade de Nielsen e padrões Material Design / Cupertino.

---

## Sumário

1. [Pré-requisitos](#pré-requisitos)
2. [Configuração Inicial](#configuração-inicial)
3. [Como Executar](#como-executar)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Telas (Screens)](#telas-screens)
6. [Serviços (Services)](#serviços-services)
7. [Modelos (Models)](#modelos-models)
8. [Widgets](#widgets)
9. [Autenticação com Keycloak](#autenticação-com-keycloak)
10. [Temas Claro e Escuro](#temas-claro-e-escuro)
11. [Assets](#assets)
12. [Build para Produção](#build-para-produção)
13. [Dependências](#dependências)

---

## Pré-requisitos

- **Flutter SDK** `^3.11.5` — [Instalar Flutter](https://flutter.dev/docs/get-started/install)
- **Dart SDK** (incluído no Flutter)
- **Android Studio** ou **Xcode** (para emuladores)
- **BFF rodando** — veja [sipLog-BFF/README.md](https://github.com/Mendes1801/sipLog/tree/main/sipLog-BFF)
- **Keycloak rodando** — veja [infra/README.md](https://github.com/Mendes1801/sipLog/tree/main/infra)

---

## Configuração Inicial

Antes de rodar o app, configure as URLs dos serviços backend nos dois arquivos abaixo:

### 1. URL do BFF

Arquivo: `lib/services/http_api_service.dart`

```dart
final String baseUrl = 'http://<IP_DO_BFF>:8081/api/v1';
```

### 2. URL do Keycloak (IAM)

Arquivo: `lib/services/auth_service.dart`

```dart
static const String _issuer = 'http://<IP_DO_KEYCLOAK>:8080/realms/BFF';
static const String _clientId = 'sipLog';
static const String _redirectUrl = 'com.example.siplogmobile://oauth2redirect';
```

> **Emulador Android:** use `10.0.2.2` como IP para apontar ao `localhost` da máquina host.
> **Dispositivo físico / emulador iOS:** use o IP da máquina na rede local (ex.: `192.168.1.10`).

---

## Como Executar

```bash
# 1. Entre no diretório do projeto Flutter
cd mobile/front/sip_log_mobile

# 2. Instale as dependências Dart
flutter pub get

# 3. Verifique o ambiente
flutter doctor

# 4. Liste dispositivos disponíveis
flutter devices

# 5. Rode no dispositivo/emulador escolhido
flutter run

# Ou escolha a plataforma explicitamente:
flutter run -d android
flutter run -d ios
flutter run -d chrome   # Web
```

---

## Estrutura do Projeto

```
mobile/front/sip_log_mobile/
│
├── lib/                        ← Todo o código Dart
│   ├── main.dart               ← Ponto de entrada; configura Provider e roteamento
│   ├── models/                 ← Classes de dados (PODOs Dart)
│   ├── screens/                ← Telas da aplicação
│   ├── services/               ← Integração com BFF e Keycloak
│   └── widgets/                ← Componentes visuais reutilizáveis
│
├── assets/
│   ├── fonts/
│   │   └── BaksoSapi.otf       ← Fonte customizada usada no logotipo
│   └── images/
│       ├── Base PNG.png        ← Tela de splash/base
│       ├── BT_AdcFoto.png      ← Botão adicionar foto
│       ├── BT_Amigos.png       ← Botão amigos
│       ├── BT_Mapa.png         ← Botão mapa
│       ├── BT_Notificação.png  ← Botão notificações
│       ├── BT_Pesquisar.png    ← Botão busca
│       ├── BT_Profile.png      ← Botão perfil
│       └── BT_Vinho.png        ← Botão vinho
│
├── API_Contracts/              ← Contratos OpenAPI usados durante o desenvolvimento
│   ├── api-docsV9.json
│   ├── api-docsV10.json
│   └── api-docsV11.json
│
├── android/                    ← Configuração nativa Android (Kotlin)
├── ios/                        ← Configuração nativa iOS (Swift)
├── web/                        ← Configuração para Flutter Web
├── linux/ · macos/ · windows/  ← Targets desktop
│
├── pubspec.yaml                ← Dependências e assets declarados
└── analysis_options.yaml       ← Configuração do linter (flutter_lints)
```

---

## Telas (Screens)

| Arquivo | Tela | Descrição |
|---|---|---|
| `login_screen.dart` | Login | Botão que dispara o fluxo OIDC com Keycloak via `flutter_appauth` |
| `feed_screen.dart` | Feed Global | Lista paginada de experiências públicas |
| `amigos_feed_screen.dart` | Feed de Amigos | Apenas postagens de quem o usuário segue |
| `post_detail_screen.dart` | Detalhe do Post | Curtidas, comentários, mapa de localização |
| `nova_experiencia_screen.dart` | Nova Experiência | Formulário: bebida, nota, foto, localização, visibilidade |
| `nova_bebida_screen.dart` | Cadastrar Bebida | Formulário para adicionar bebida ao catálogo |
| `profile_screen.dart` | Meu Perfil | Avatar, bio, lista de experiências pessoais, seguidores |
| `edit_profile_screen.dart` | Editar Perfil | Atualizar nome, bio e foto de avatar |
| `user_profile_screen.dart` | Perfil de Outro Usuário | Visualização pública, botão seguir/deixar de seguir |
| `busca_usuarios_screen.dart` | Buscar Usuários | Campo de busca com resultados em tempo real |
| `user_list_screen.dart` | Lista de Usuários | Reutilizada para seguidores e seguindo |
| `notificacao_screen.dart` | Notificações | Lista de curtidas, comentários e novos seguidores |
| `configuracoes_screen.dart` | Configurações | Toggle de tema claro/escuro, logout |

---

## Serviços (Services)

| Arquivo | Responsabilidade |
|---|---|
| `auth_service.dart` | Fluxo OIDC com Keycloak; armazenamento seguro de tokens; renovação automática (`refreshToken`); logout |
| `http_api_service.dart` | Cliente HTTP base: GET, POST, PUT, PATCH, DELETE, upload multipart; gerencia headers e tratamento de erros |
| `http_feed_service.dart` | Chamadas ao feed global, feed de amigos e feed de usuário |
| `http_user_service.dart` | Sincronização, busca, atualização e remoção de usuários |
| `http_experiencia_service.dart` | CRUD de experiências, curtidas e comentários |
| `http_bebida_service.dart` | Busca e cadastro de bebidas |
| `http_notificacao_service.dart` | Listagem e marcação de notificações |
| `theme_service.dart` | Gerenciamento do tema claro/escuro com `ChangeNotifier` |
| `mock_feed_service.dart` | Dados mockados para desenvolvimento offline |
| `mock_comentario_service.dart` | Comentários mockados para testes |
| `mock_user_service.dart` | Usuário mockado para testes |

---

## Modelos (Models)

| Arquivo | Representa |
|---|---|
| `user_models.dart` | Perfil do usuário, resumo de usuário |
| `bebida_models.dart` | Bebida com atributos (nome, categoria, fabricante, `caracteristicas`) |
| `experiencia_models.dart` | Experiência com nota, comentário, foto, localização e visibilidade |
| `feed_response_model.dart` | Item do feed com dados de autor, bebida, engajamento e tempo decorrido |
| `comentario_model.dart` | Comentário de uma experiência |
| `notificacao_models.dart` | Notificação com tipo, data e status de leitura |

Todos os modelos possuem factory `fromJson()` para desserialização das respostas do BFF.

---

## Autenticação com Keycloak

O fluxo de autenticação usa o pacote `flutter_appauth` (OIDC Authorization Code Flow com PKCE):

```dart
// auth_service.dart (simplificado)
final result = await _appAuth.authorizeAndExchangeCode(
  AuthorizationTokenRequest(
    'sipLog',                                    // Client ID
    'com.example.siplogmobile://oauth2redirect', // Redirect URI
    serviceConfiguration: _config,
    scopes: ['openid', 'profile', 'email', 'offline_access'],
  ),
);
```

Após o login:
1. O `accessToken` é armazenado em memória e o `refreshToken` no `flutter_secure_storage`
2. O método `getValidAccessToken()` renova o token automaticamente antes do vencimento
3. O `AuthService` é exposto via `Provider` para toda a árvore de widgets

### Configuração do Android para Deep Link

O `AndroidManifest.xml` já está configurado para capturar o redirect URI:
```xml
<!-- android/app/src/main/AndroidManifest.xml -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <data android:scheme="com.example.siplogmobile" android:host="oauth2redirect" />
</intent-filter>
```

---

## Temas Claro e Escuro

O `ThemeService` (`lib/services/theme_service.dart`) gerencia o tema e expõe via `ChangeNotifier`. A preferência é persistida localmente.

```dart
// Alternar tema na tela de configurações
context.read<ThemeService>().toggleTheme();
```

---

## Assets

### Fonte Customizada

A fonte **BaksoSapi** (OTF) é declarada no `pubspec.yaml` e usada no logotipo do app:

```yaml
fonts:
  - family: BaksoSapi
    fonts:
      - asset: assets/fonts/BaksoSapi.otf
```

### Imagens

Todas as imagens declaradas no `pubspec.yaml` ficam em `assets/images/` e são referenciadas via `Image.asset('assets/images/Base PNG.png')`.

---

## Build para Produção

### Android (APK / AAB)

```bash
cd mobile/front/sip_log_mobile

# APK (instalação direta)
flutter build apk --release

# AAB (Google Play Store)
flutter build appbundle --release
```

O APK gerado fica em `build/app/outputs/flutter-apk/app-release.apk`.

### iOS (necessita macOS + Xcode)

```bash
flutter build ios --release
```

Abra `ios/Runner.xcworkspace` no Xcode para assinar e publicar na App Store.

### Web

```bash
flutter build web --release
```

Os arquivos ficam em `build/web/` e podem ser servidos por qualquer servidor estático.

---

## Dependências

Declaradas em `pubspec.yaml`:

| Pacote | Versão | Propósito |
|---|---|---|
| `flutter_appauth` | `^12.0.1` | Autenticação OIDC com Keycloak |
| `flutter_secure_storage` | `^10.3.0` | Armazenamento seguro de tokens |
| `http` | `^1.6.0` | Chamadas REST ao BFF |
| `provider` | `^6.1.1` | Gerenciamento de estado (`ChangeNotifier`) |
| `flutter_map` | `^8.3.0` | Exibição de mapas (localização da experiência) |
| `latlong2` | `^0.9.0` | Coordenadas geográficas |
| `image_picker` | `^1.1.5` | Seleção de fotos da galeria/câmera |
| `url_launcher` | `^6.3.2` | Abrir links externos |
| `cupertino_icons` | `^1.0.8` | Ícones iOS style |

Dev dependencies:
| Pacote | Propósito |
|---|---|
| `flutter_lints` | Conjunto de regras de linting recomendadas |
| `flutter_test` | Framework de testes de widget |

---

> **SDK Flutter:** `^3.11.5`
> **Plataformas suportadas:** Android · iOS · Web · Linux · macOS · Windows
> **Comunicação com backend:** REST via `http_api_service.dart` + OIDC via `auth_service.dart`
