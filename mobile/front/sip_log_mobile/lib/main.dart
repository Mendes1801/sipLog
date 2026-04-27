import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SIP Log Mobile',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SIP Log'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Imagem base
            Image.asset('assets/images/Base PNG.png'),
            const SizedBox(height: 20),
            // Botões usando as imagens da documentação
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _buildButton('assets/images/BT_Vinho.png', 'Vinhos'),
                _buildButton('assets/images/BT_Profile.png', 'Perfil'),
                _buildButton('assets/images/BT_Amigos.png', 'Amigos'),
              ],
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _buildButton('assets/images/BT_Mapa.png', 'Mapa'),
                _buildButton('assets/images/BT_Notificação.png', 'Notificações'),
                _buildButton('assets/images/BT_Pesquisar.png', 'Pesquisar'),
              ],
            ),
            const SizedBox(height: 20),
            _buildButton('assets/images/BT_AdcFoto.png', 'Adicionar Foto'),
          ],
        ),
      ),
    );
  }

  Widget _buildButton(String imagePath, String label) {
    return Column(
      children: [
        Image.asset(imagePath, width: 50, height: 50),
        const SizedBox(height: 8),
        Text(label, style: const TextStyle(fontSize: 12)),
      ],
    );
  }
}
