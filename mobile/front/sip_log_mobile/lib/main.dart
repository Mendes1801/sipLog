import 'package:flutter/material.dart';
import 'screens/feed_screen.dart';
import 'screens/nova_experiencia_screen.dart';
import 'screens/profile_screen.dart';

void main() {
  runApp(const SipLogApp());
}

class SipLogApp extends StatelessWidget {
  const SipLogApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SipLog',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
        useMaterial3: true,
      ),
      home: const TelaNavegacaoBase(),
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

  Widget _construirTelaAtual() {
    switch (_indiceAtual) {
      case 0:
        return FeedScreen(key: _feedKey); 
      case 1:
        return const Center(child: Text('Pesquisa', style: TextStyle(fontSize: 20)));
      case 3:
        return const Center(child: Text('Notificações', style: TextStyle(fontSize: 20)));
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
      ),
      body: _construirTelaAtual(),
      
      // O Botão Flutuante (FAB) e a BottomAppBar foram completamente removidos!
      
      // Usando o BottomNavigationBar tradicional
      bottomNavigationBar: BottomNavigationBar(
        elevation: 8, // Restaura a sombra para destacar a barra
        backgroundColor: Colors.white,
        currentIndex: _indiceAtual == 2 ? 0 : _indiceAtual, 
        type: BottomNavigationBarType.fixed,
        selectedItemColor: Colors.deepPurple,
        unselectedItemColor: Colors.grey,
        onTap: (index) {
          if (index == 2) {
            // Se clicar no botão do meio (Postar), ele intercepta o clique e abre a tela nova
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const NovaExperienciaScreen()),
            ).then((postou) {
              if (postou == true) {
                setState(() {
                  _feedKey = UniqueKey(); 
                  _indiceAtual = 0; // Garante que você caia no feed após postar
                }); 
              }
            });
          } else {
            // Se clicar nos outros, faz a navegação normal
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
          
          // O NOVO BOTÃO CENTRAL ALINHADO
          const BottomNavigationBarItem(
            icon: Icon(Icons.add_circle, size: 40, color: Colors.deepPurple), // Um "+" grande e destacado
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