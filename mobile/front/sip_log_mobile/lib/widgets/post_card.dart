import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../models/feed_response_model.dart';
import '../screens/post_detail_screen.dart';

// O "Remendo": Um cache global temporário na memória para as telas conversarem
final Map<int, Map<String, dynamic>> mockStateCache = {};

class PostCard extends StatefulWidget {
  final FeedResponseModel post;
  final bool isDetailScreen;
  final VoidCallback? onComentarioAdicionado; // Avisa o card se um comentário novo for feito

  const PostCard({
    super.key, 
    required this.post, 
    this.isDetailScreen = false,
    this.onComentarioAdicionado,
  });

  @override
  State<PostCard> createState() => _PostCardState();
}

class _PostCardState extends State<PostCard> {
  late bool _curtido;
  late int _totalCurtidas;
  late int _totalComentarios;

  // Função que lê os dados do cache centralizado
  void _carregarEstado() {
    int id = widget.post.idPost;
    if (!mockStateCache.containsKey(id)) {
      mockStateCache[id] = {
        'curtido': widget.post.curtidoPorMim,
        'totalCurtidas': widget.post.totalCurtidas,
        'totalComentarios': widget.post.totalComentarios,
      };
    }
    _curtido = mockStateCache[id]!['curtido'];
    _totalCurtidas = mockStateCache[id]!['totalCurtidas'];
    _totalComentarios = mockStateCache[id]!['totalComentarios'];
  }

  @override
  void initState() {
    super.initState();
    _carregarEstado();
  }

  Future<void> _alternarCurtida() async {
    setState(() {
      _curtido = !_curtido;
      _totalCurtidas += _curtido ? 1 : -1;
      
      // Salva no cache para a outra tela enxergar
      mockStateCache[widget.post.idPost]!['curtido'] = _curtido;
      mockStateCache[widget.post.idPost]!['totalCurtidas'] = _totalCurtidas;
    });

    try {
      final url = Uri.parse('http://localhost:8081/api/v1/experiencias/${widget.post.idPost}/curtir');
      final response = await http.post(url);
      if (response.statusCode != 200) {
        // Em produção, desfazemos aqui
      }
    } catch (e) {
      // Silenciado para o nosso teste mockado
    }
  }

  @override
  Widget build(BuildContext context) {
    // Se o callback for disparado pela tela de detalhes, atualiza o nosso estado aqui
    if (widget.isDetailScreen) {
      _carregarEstado(); 
    }

    return Card(
      margin: widget.isDetailScreen 
          ? EdgeInsets.zero 
          : const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
      elevation: widget.isDetailScreen ? 0 : 4,
      shape: widget.isDetailScreen 
          ? const RoundedRectangleBorder(borderRadius: BorderRadius.zero)
          : RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
      child: Padding(
        padding: const EdgeInsets.all(15.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundImage: widget.post.fotoAvatarUrl != null
                      ? NetworkImage(widget.post.fotoAvatarUrl!)
                      : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                  radius: 22,
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(widget.post.nomeAutor, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      Text('${widget.post.local ?? "Desconhecido"} • ${widget.post.tempoDecorrido}', 
                           style: const TextStyle(color: Colors.grey, fontSize: 12)),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            
            if (widget.post.fotoPostUrl != null)
              ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: Image.network(widget.post.fotoPostUrl!, width: double.infinity, height: 220, fit: BoxFit.cover),
              ),
            const SizedBox(height: 12),
            
            Text(widget.post.nomeBebida, 
                 style: const TextStyle(fontFamily: 'BaksoSapi', fontSize: 24, color: Colors.deepPurple)),
            Text(widget.post.categoriaBebida, style: const TextStyle(fontStyle: FontStyle.italic, color: Colors.grey)),
            const SizedBox(height: 8),
            
            Row(
              children: [
                const Icon(Icons.star, color: Colors.amber, size: 20),
                Text(' ${widget.post.nota.toString()} / 5.0', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              ],
            ),
            
            if (widget.post.comentario != null) ...[
              const SizedBox(height: 8),
              Text('"${widget.post.comentario!}"', style: const TextStyle(fontSize: 15)),
            ],
            
            const Divider(height: 30),
            
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                GestureDetector(
                  onTap: _alternarCurtida,
                  child: Row(
                    children: [
                      Icon(_curtido ? Icons.favorite : Icons.favorite_border, 
                           color: _curtido ? Colors.red : Colors.grey),
                      const SizedBox(width: 5),
                      Text('$_totalCurtidas Curtidas', 
                           style: TextStyle(color: _curtido ? Colors.red : Colors.grey, fontWeight: FontWeight.bold)),
                    ],
                  ),
                ),
                
                GestureDetector(
                  onTap: () {
                    if (!widget.isDetailScreen) {
                      // O .then() é a mágica: ele roda assim que a tela de detalhes é fechada!
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => PostDetailScreen(post: widget.post),
                        ),
                      ).then((_) {
                        // Força o Feed a ler o cache novamente e se redesenhar
                        setState(() {
                          _carregarEstado();
                        });
                      });
                    }
                  },
                  child: Row(
                    children: [
                      const Icon(Icons.chat_bubble_outline, color: Colors.grey),
                      const SizedBox(width: 5),
                      Text('$_totalComentarios Comentários'),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}