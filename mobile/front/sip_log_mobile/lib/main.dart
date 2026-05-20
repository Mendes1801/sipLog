import 'package:flutter/material.dart';
import 'screens/feed_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SipLog',
      debugShowCheckedModeBanner: false, // Tira aquela faixa chata de "DEBUG" da tela
      theme: ThemeData(
        primarySwatch: Colors.deepPurple, // Podemos ajustar para as cores exatas do seu Figma/Design depois
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

  
  final List<Widget> _telas = [
    const FeedScreen(), // A Mágica Acontece Aqui!
    const Center(child: Text('Pesquisa', style: TextStyle(fontSize: 20))),
    const Center(child: Text('Notificações', style: TextStyle(fontSize: 20))),
    const Center(child: Text('Perfil', style: TextStyle(fontSize: 20))),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SipLog'),
        centerTitle: true,
      ),
      body: _telas[_indiceAtual],
bottomNavigationBar: BottomNavigationBar(
        currentIndex: _indiceAtual,
        type: BottomNavigationBarType.fixed,
        backgroundColor: Colors.white,
        selectedItemColor: Colors.deepPurple, // A cor do texto quando selecionado
        unselectedItemColor: Colors.grey,
        onTap: (index) {
          setState(() {
            _indiceAtual = index;
          });
        },
        items: [
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Vinho.png', width: 28, height: 28), // Usando o ícone de Vinho/Bebida para o Feed
            label: 'Feed',
          ),
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Pesquisar.png', width: 28, height: 28),
            label: 'Buscar',
          ),
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Notificação.png', width: 28, height: 28),
            label: 'Avisos',
          ),
          BottomNavigationBarItem(
            icon: Image.asset('assets/images/BT_Profile.png', width: 28, height: 28),
            label: 'Perfil',
          ),
        ],
      ),
    );
  }
}