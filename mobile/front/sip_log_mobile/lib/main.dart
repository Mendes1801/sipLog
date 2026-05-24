import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'screens/busca_usuarios_screen.dart';
import 'screens/feed_screen.dart';
import 'screens/nova_experiencia_screen.dart';
import 'screens/notificacao_screen.dart';
import 'screens/profile_screen.dart';
import 'screens/login_screen.dart';
import 'services/auth_service.dart';
import 'services/http_user_service.dart';
import 'services/http_feed_service.dart';
import 'services/http_experiencia_service.dart';
import 'services/http_notificacao_service.dart';
import 'services/http_bebida_service.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => AuthService()..tryAutoLogin(),
      child: const SipLogApp(),
    ),
  );
}

class SipLogApp extends StatelessWidget {
  const SipLogApp({super.key});

  @override
  Widget build(BuildContext context) {
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
        theme: ThemeData(
          primarySwatch: Colors.deepPurple,
          useMaterial3: true,
        ),
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
      Provider.of<HttpUserService>(context, listen: false).sincronizarUsuario().catchError((e) {
        debugPrint('Erro na sincronização inicial: $e');
      });
    });
  }

  Widget _construirTelaAtual() {
    switch (_indiceAtual) {
      case 0:
        return FeedScreen(key: _feedKey);
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
    return Scaffold(
      appBar: AppBar(
        title: const Text('SipLog', style: TextStyle(fontFamily: 'BaksoSapi', fontSize: 28)),
        centerTitle: true,
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
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
      bottomNavigationBar: BottomNavigationBar(
        elevation: 8,
        backgroundColor: Colors.white,
        currentIndex: _indiceAtual == 2 ? 0 : _indiceAtual,
        type: BottomNavigationBarType.fixed,
        selectedItemColor: Colors.deepPurple,
        unselectedItemColor: Colors.grey,
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
            icon: Image.asset('assets/images/BT_Vinho.png', width: 28, color: _indiceAtual == 0 ? Colors.deepPurple : null),
            label: 'Feed'
          ),
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Pesquisar.png', width: 28, color: _indiceAtual == 1 ? Colors.deepPurple : null),
            label: 'Busca'
          ),
          const BottomNavigationBarItem(
            icon: Icon(Icons.add_circle, size: 40, color: Colors.deepPurple),
            label: 'Postar'
          ),
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Notificação.png', width: 28, color: _indiceAtual == 3 ? Colors.deepPurple : null),
            label: 'Avisos'
          ),
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Profile.png', width: 28, color: _indiceAtual == 4 ? Colors.deepPurple : null),
            label: 'Perfil'
          ),
        ],
      ),
    );
  }
}
