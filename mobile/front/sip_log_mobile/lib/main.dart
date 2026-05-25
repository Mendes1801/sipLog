import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'screens/busca_usuarios_screen.dart';
import 'screens/feed_screen.dart';
import 'screens/nova_experiencia_screen.dart';
import 'screens/amigos_feed_screen.dart';
import 'screens/notificacao_screen.dart';
import 'screens/profile_screen.dart';
import 'screens/login_screen.dart';
import 'services/theme_service.dart';
import 'services/auth_service.dart';
import 'services/http_user_service.dart';
import 'services/http_feed_service.dart';
import 'services/http_experiencia_service.dart';
import 'services/http_notificacao_service.dart';
import 'services/http_bebida_service.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => ThemeService()),
        ChangeNotifierProvider(create: (_) => AuthService()..tryAutoLogin()),
      ],
      child: const SipLogApp(),
    ),
  );
}

class SipLogApp extends StatelessWidget {
  const SipLogApp({super.key});

  @override
  Widget build(BuildContext context) {
    final themeService = Provider.of<ThemeService>(context);

    return MultiProvider(
      providers: [
        ProxyProvider<AuthService, HttpUserService>(
          update: (_, auth, __) => HttpUserService(auth),
        ),
        ProxyProvider<AuthService, HttpFeedService>(
          update: (_, auth, __) => HttpFeedService(auth),
        ),
        ProxyProvider<AuthService, HttpExperienciaService>(
          update: (_, auth, __) => HttpExperienciaService(auth),
        ),
        ProxyProvider<AuthService, HttpNotificacaoService>(
          update: (_, auth, __) => HttpNotificacaoService(auth),
        ),
        ProxyProvider<AuthService, HttpBebidaService>(
          update: (_, auth, __) => HttpBebidaService(auth),
        ),
      ],
      child: MaterialApp(
        title: 'SipLog',
        debugShowCheckedModeBanner: false,
        themeMode: themeService.themeMode,
        theme: themeService.getThemeData(false),
        darkTheme: themeService.getThemeData(true),
        home: Consumer<AuthService>(
          builder: (context, auth, _) {
            if (auth.isAuthenticated) {
              return const TelaNavegacaoBase();
            } else {
              return const LoginScreen();
            }
          },
        ),
      ),
    );
  }
}

class TelaNavegacaoBase extends StatefulWidget {
  const TelaNavegacaoBase({super.key});

  @override
  State<TelaNavegacaoBase> createState() => _TelaNavegacaoBaseState();
}

class _TelaNavegacaoBaseState extends State<TelaNavegacaoBase> {
  int _indiceAtual = 0;
  UniqueKey _feedKey = UniqueKey();

  @override
  void initState() {
    super.initState();
    // Garante a sincronização do usuário com o backend logo na entrada
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _inicializarDados();
    });
  }

  Future<void> _inicializarDados() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    final authService = Provider.of<AuthService>(context, listen: false);
    try {
      await userService.sincronizarUsuario();
      final perfil = await userService.getMeuPerfil();
      if (mounted) {
        authService.updateUserData(
          avatarUrl: perfil.usuario?.fotoAvatarUrl,
          id: perfil.usuario?.idUsuario,
        );
      }
    } catch (e) {
      debugPrint('Erro na inicialização: $e');
    }
  }

  Widget _construirTelaAtual() {
    switch (_indiceAtual) {
      case 0:
        return TabBarView(
          children: [
            FeedScreen(key: _feedKey),
            const AmigosFeedScreen(),
          ],
        );
      case 1:
        return const BuscaUsuariosScreen();
      case 3:
        return const NotificacaoScreen();
      case 4:
        return const ProfileScreen();
      default:
        return const SizedBox.shrink();
    }
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('SipLog', style: TextStyle(fontFamily: 'BaksoSapi', fontSize: 28)),
          bottom: _indiceAtual == 0 
            ? const TabBar(
                tabs: [
                  Tab(text: 'Global'),
                  Tab(text: 'Amigos'),
                ],
              )
            : null,
          actions: [
            IconButton(
              icon: const Icon(Icons.logout),
              onPressed: () {
                Provider.of<AuthService>(context, listen: false).logout();
              },
            ),
          ],
        ),
        body: _construirTelaAtual(),
      bottomNavigationBar: Consumer<AuthService>(
        builder: (context, auth, _) => BottomNavigationBar(
          currentIndex: _indiceAtual == 2 ? 0 : _indiceAtual,
          onTap: (index) {
            if (index == 2) {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const NovaExperienciaScreen()),
              ).then((postou) {
                if (postou == true) {
                  setState(() {
                    _feedKey = UniqueKey();
                    _indiceAtual = 0;
                  });
                }
              });
            } else {
              setState(() => _indiceAtual = index);
            }
          },
          items: [
            BottomNavigationBarItem(
              icon: Opacity(opacity: 0.5, child: Image.asset('assets/images/BT_Vinho.png', width: 26)),
              activeIcon: Image.asset('assets/images/BT_Vinho.png', width: 30),
              label: 'Feed'
            ),
            BottomNavigationBarItem(
              icon: Opacity(opacity: 0.5, child: Image.asset('assets/images/BT_Pesquisar.png', width: 26)),
              activeIcon: Image.asset('assets/images/BT_Pesquisar.png', width: 30),
              label: 'Busca'
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.add_circle, size: 42, color: Theme.of(context).colorScheme.primary),
              label: 'Postar'
            ),
            BottomNavigationBarItem(
              icon: Opacity(opacity: 0.5, child: Image.asset('assets/images/BT_Notificação.png', width: 26)),
              activeIcon: Image.asset('assets/images/BT_Notificação.png', width: 30),
              label: 'Avisos'
            ),
            BottomNavigationBarItem(
              icon: CircleAvatar(
                radius: 14,
                backgroundColor: _indiceAtual == 4 ? Theme.of(context).colorScheme.primary : Colors.grey.withOpacity(0.3),
                child: CircleAvatar(
                  radius: 13,
                  backgroundImage: auth.userAvatarUrl != null && auth.userAvatarUrl!.isNotEmpty
                      ? NetworkImage(auth.userAvatarUrl!)
                      : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                ),
              ),
              label: 'Perfil'
            ),
          ],
        ),
      ),
      ),
    );
  }
}
