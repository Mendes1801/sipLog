import 'package:flutter/material.dart';
import '../models/feed_response_model.dart';
import '../models/comentario_model.dart';
import '../services/mock_comentario_service.dart';
import '../widgets/post_card.dart';

class PostDetailScreen extends StatefulWidget {
  final FeedResponseModel post;

  const PostDetailScreen({super.key, required this.post});

  @override
  State<PostDetailScreen> createState() => _PostDetailScreenState();
}

class _PostDetailScreenState extends State<PostDetailScreen> {
  final MockComentarioService _comentarioService = MockComentarioService();
  final TextEditingController _comentarioController = TextEditingController();
  
  List<ComentarioModel> _comentarios = [];
  bool _carregando = true;
  bool _enviando = false;

  @override
  void initState() {
    super.initState();
    _carregarComentarios();
  }

  Future<void> _carregarComentarios() async {
    final coments = await _comentarioService.getComentarios(widget.post.idPost);
    setState(() {
      _comentarios = coments;
      _carregando = false;
    });
  }

  Future<void> _enviarComentario() async {
    if (_comentarioController.text.trim().isEmpty) return;
    
    setState(() => _enviando = true);
    
    final novo = await _comentarioService.adicionarComentario(widget.post.idPost, _comentarioController.text);
    
    // Atualiza o cache global do remendo para a contagem de comentários subir
    if (mockStateCache.containsKey(widget.post.idPost)) {
       mockStateCache[widget.post.idPost]!['totalComentarios'] += 1;
    }

    setState(() {
      _comentarios.insert(0, novo);
      _comentarioController.clear();
      _enviando = false;
    });
    
    FocusScope.of(context).unfocus();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Degustação', style: TextStyle(fontFamily: 'BaksoSapi')),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView(
              children: [
                PostCard(
                  post: widget.post, 
                  isDetailScreen: true,
                ),
                
                const Padding(
                  padding: EdgeInsets.all(15.0),
                  child: Text('Comentários', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                ),
                
                if (_carregando)
                  const Center(child: Padding(padding: EdgeInsets.all(20.0), child: CircularProgressIndicator()))
                else if (_comentarios.isEmpty)
                  const Center(child: Padding(padding: EdgeInsets.all(20.0), child: Text('Seja o primeiro a comentar!')))
                else
                  ..._comentarios.map((c) => ListTile(
                    leading: CircleAvatar(
                      backgroundImage: c.autorFoto != null 
                        ? NetworkImage(c.autorFoto!) 
                        : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                    ),
                    title: Row(
                      children: [
                        Text(c.autorNome, style: const TextStyle(fontWeight: FontWeight.bold)),
                        const SizedBox(width: 8),
                        Text(c.tempoDecorrido, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                      ],
                    ),
                    subtitle: Text(c.texto),
                  )),
              ],
            ),
          ),
          
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
            decoration: BoxDecoration(color: Colors.white, boxShadow: [BoxShadow(color: Colors.grey.shade300, blurRadius: 4)]),
            child: SafeArea(
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _comentarioController,
                      decoration: InputDecoration(
                        hintText: 'Adicione um comentário...',
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(20)),
                        contentPadding: const EdgeInsets.symmetric(horizontal: 15, vertical: 10),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  _enviando 
                    ? const CircularProgressIndicator()
                    : IconButton(
                        icon: const Icon(Icons.send, color: Colors.deepPurple),
                        onPressed: _enviarComentario,
                      ),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}