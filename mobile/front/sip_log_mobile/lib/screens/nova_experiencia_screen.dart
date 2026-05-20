import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart'; // Import necessário
import 'dart:io' show File; // Para lidar com arquivos (não usado diretamente na web, mas bom ter)
import 'package:flutter/foundation.dart' show kIsWeb; // Para checar se é web

import '../models/feed_response_model.dart';
import '../services/mock_feed_service.dart';

class NovaExperienciaScreen extends StatefulWidget {
  const NovaExperienciaScreen({super.key});

  @override
  State<NovaExperienciaScreen> createState() => _NovaExperienciaScreenState();
}

class _NovaExperienciaScreenState extends State<NovaExperienciaScreen> {
  final _nomeController = TextEditingController();
  final _comentarioController = TextEditingController();
  double _nota = 5.0;
  
  // Estado para guardar a imagem selecionada
  XFile? _imagemSelecionada;
  final ImagePicker _picker = ImagePicker();

  // Função para abrir o seletor de imagens
  Future<void> _selecionarImagem() async {
    try {
      // Abre a galeria (no navegador, abre o seletor de arquivos)
      final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
      
      if (image != null) {
        setState(() {
          _imagemSelecionada = image; // Atualiza a tela com a foto nova
        });
      }
    } catch (e) {
      debugPrint('Erro ao selecionar imagem: $e');
    }
  }

  void _postar() {
    if (_nomeController.text.trim().isEmpty) return;

    // Foto Padrão caso o usuário não selecione nenhuma
    String fotoParaOPost = 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?auto=format&fit=crop&w=500&q=60';

    if (_imagemSelecionada != null) {
      // Na web, o path do XFile é uma URL blob (ex: blob:http://localhost:8080/...)
      // que o navegador consegue renderizar. Perfeito para o nosso mock visual.
      fotoParaOPost = _imagemSelecionada!.path;
    }

    final novoPost = FeedResponseModel(
      idPost: DateTime.now().millisecondsSinceEpoch,
      idUsuario: 999,
      nomeAutor: 'Você',
      fotoAvatarUrl: null, // Usará ícone default BT_Profile.png
      tempoDecorrido: 'Agora mesmo',
      local: 'Adicionado recentemente',
      fotoPostUrl: fotoParaOPost, // USA A FOTO SELECIONADA AQUI!
      idBebida: 0, 
      nomeBebida: _nomeController.text,
      categoriaBebida: 'Degustação',
      nota: _nota,
      comentario: _comentarioController.text,
      curtidoPorMim: false,
      totalCurtidas: 0,
      totalComentarios: 0,
    );

    MockFeedService.adicionarNovoPost(novoPost);
    Navigator.pop(context, true);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Nova Experiência', style: TextStyle(fontFamily: 'BaksoSapi')),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // ÁREA DA FOTO TORNADA CLICÁVEL
            GestureDetector(
              onTap: _selecionarImagem, // Chama a função ao clicar
              child: Container(
                height: 200,
                width: double.infinity,
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(15),
                  border: Border.all(color: Colors.deepPurple.withOpacity(0.3)),
                ),
                // Lógica visual: mostra a foto se selecionada, senão mostra o ícone de "+"
                child: _imagemSelecionada != null
                    ? ClipRRect(
                        borderRadius: BorderRadius.circular(15),
                        // Na web, Image.network consegue ler a blob URL do XFile.path
                        child: Image.network(_imagemSelecionada!.path, fit: BoxFit.cover, width: double.infinity,),
                      )
                    : Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.add_a_photo, size: 60, color: Colors.deepPurple),
                          const SizedBox(height: 10),
                          const Text('Toque para adicionar foto', style: TextStyle(color: Colors.deepPurple, fontWeight: FontWeight.bold)),
                        ],
                      ),
              ),
            ),
            const SizedBox(height: 30),
            
            TextField(
              controller: _nomeController,
              decoration: const InputDecoration(
                labelText: 'O que você está bebendo?',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 20),
            
            const Text('Sua Nota:', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            Slider(
              value: _nota,
              min: 1,
              max: 5,
              divisions: 8, 
              label: _nota.toStringAsFixed(1),
              onChanged: (v) => setState(() => _nota = v),
              activeColor: Colors.amber,
            ),
            
            TextField(
              controller: _comentarioController,
              maxLines: 3,
              decoration: const InputDecoration(
                labelText: 'Comentário (opcional)',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 30),
            
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: _postar,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))
                ),
                child: const Text('Publicar no Feed', style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}