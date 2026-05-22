import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart'; 
import '../models/feed_response_model.dart';
import '../services/mock_feed_service.dart';

class NovaExperienciaScreen extends StatefulWidget {
  const NovaExperienciaScreen({super.key});

  @override
  State<NovaExperienciaScreen> createState() => _NovaExperienciaScreenState();
}

class _NovaExperienciaScreenState extends State<NovaExperienciaScreen> {
  final _comentarioController = TextEditingController();
  double _nota = 5.0;
  
  XFile? _imagemSelecionada;
  final ImagePicker _picker = ImagePicker();

  // NOVO: Simulação do que viria do endpoint /api/v1/bebidas
  final List<Map<String, dynamic>> _bebidasDisponiveis = [
    {'id': 1, 'nome': 'Vinho Cabernet Sauvignon', 'categoria': 'Vinho Tinto'},
    {'id': 2, 'nome': 'IPA Artesanal', 'categoria': 'Cerveja'},
    {'id': 3, 'nome': 'Whisky Single Malt', 'categoria': 'Destilado'},
    {'id': 4, 'nome': 'Gin Tônica', 'categoria': 'Coquetel'},
  ];
  
  // Variável para armazenar a bebida selecionada
  Map<String, dynamic>? _bebidaSelecionada;

  Future<void> _selecionarImagem() async {
    try {
      final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
      if (image != null) {
        setState(() {
          _imagemSelecionada = image; 
        });
      }
    } catch (e) {
      debugPrint('Erro ao selecionar imagem: $e');
    }
  }

  void _postar() {
    // Validação: Exige que uma bebida seja escolhida
    if (_bebidaSelecionada == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Por favor, selecione uma bebida!')),
      );
      return;
    }

    String fotoParaOPost = 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?auto=format&fit=crop&w=500&q=60';

    if (_imagemSelecionada != null) {
      fotoParaOPost = _imagemSelecionada!.path;
    }

    final novoPost = FeedResponseModel(
      idPost: DateTime.now().millisecondsSinceEpoch,
      idUsuario: 999, // Seu ID
      nomeAutor: 'Você',
      fotoAvatarUrl: null, 
      tempoDecorrido: 'Agora mesmo',
      local: 'Adicionado recentemente',
      fotoPostUrl: fotoParaOPost, 
      idBebida: _bebidaSelecionada!['id'], // USA O ID AGORA!
      nomeBebida: _bebidaSelecionada!['nome'],
      categoriaBebida: _bebidaSelecionada!['categoria'],
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
            GestureDetector(
              onTap: _selecionarImagem,
              child: Container(
                height: 200,
                width: double.infinity,
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(15),
                  border: Border.all(color: Colors.deepPurple.withOpacity(0.3)),
                ),
                child: _imagemSelecionada != null
                    ? ClipRRect(
                        borderRadius: BorderRadius.circular(15),
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
            
            // NOVO: Dropdown no lugar do TextField livre
            DropdownButtonFormField<Map<String, dynamic>>(
              decoration: const InputDecoration(
                labelText: 'Qual bebida você escolheu?',
                border: OutlineInputBorder(),
              ),
              items: _bebidasDisponiveis.map((bebida) {
                return DropdownMenuItem<Map<String, dynamic>>(
                  value: bebida,
                  child: Text(bebida['nome']),
                );
              }).toList(),
              onChanged: (valor) {
                setState(() {
                  _bebidaSelecionada = valor;
                });
              },
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