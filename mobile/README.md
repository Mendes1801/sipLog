# 📱 SipLog Mobile App

Aplicativo oficial do SipLog desenvolvido com o framework **Flutter**. A interface foi estruturada aplicando heurísticas de usabilidade (Nielsen) e padrões de design limpo (Material Design / Cupertino) para entregar uma experiência fluida aos usuários.

## Arquitetura do App
A pasta `lib/` está dividida em:
- `models/`: Classes de representação de dados (User, FeedResponse, Experiencia, Comentario).
- `screens/`: Telas e fluxo de navegação (Feed, Perfil, Busca de Usuários, Login, Adicionar Bebida).
- `services/`: Classes de integração HTTP com o BFF (`http_api_service`, `http_feed_service`, `auth_service`). Utiliza mocks temporários (`mock_feed_service`) para simulação de desenvolvimento offline quando necessário.
- `widgets/`: Componentes visuais reutilizáveis (como o `post_card`).

## Configuração e Execução
Certifique-se de que o `sipLog-BFF` esteja rodando localmente na sua máquina (ou aponte a Base URL para o IP correto na rede ou servidor de homologação).

```bash
# 1. Entre no diretório do projeto
cd front/sip_log_mobile

# 2. Atualize as dependências
flutter pub get

# 3. Rode no dispositivo conectado (Emulador iOS/Android ou Físico)
flutter run
```
