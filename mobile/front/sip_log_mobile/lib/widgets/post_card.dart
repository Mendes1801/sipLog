import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../screens/post_detail_screen.dart';
import '../services/http_experiencia_service.dart';

class PostCard extends StatefulWidget {
  final FeedResponseModel post;
  final bool isDetailScreen;
  final VoidCallback? onComentarioAdicionado;
  final VoidCallback? onPostDeletado;

  const PostCard({
    super.key, 
    required this.post, 
    this.isDetailScreen = false,
    this.onComentarioAdicionado,
    this.onPostDeletado,
  });

  @override
  State<PostCard> createState() => _PostCardState();
}

class _PostCardState extends State<PostCard> {
  late bool _curtido;
  late int _totalCurtidas;
  late int _totalComentarios;

  @override
  void initState() {
    super.initState();
    _curtido = widget.post.curtidoPorMim;
    _totalCurtidas = widget.post.totalCurtidas;
    _totalComentarios = widget.post.totalComentarios;
  }

  Future<void> _alternarCurtida() async {
    final experienciaService = Provider.of<HttpExperienciaService>(context, listen: false);

    setState(() {
      _curtido = !_curtido;
      _totalCurtidas += _curtido ? 1 : -1;
    });

    try {
      await experienciaService.alternarCurtida(widget.post.idPost);
    } catch (e) {
      // Reverter em caso de erro
      setState(() {
        _curtido = !_curtido;
        _totalCurtidas += _curtido ? 1 : -1;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao curtir: $e')),
        );
      }
    }
  }

  Future<void> _deletarPost() async {
    final experienciaService = Provider.of<HttpExperienciaService>(context, listen: false);
    try {
      await experienciaService.deletarExperiencia(widget.post.idPost);
      if (mounted) {
        if (widget.isDetailScreen) {
          Navigator.pop(context);
        }
        if (widget.onPostDeletado != null) {
          widget.onPostDeletado!();
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao deletar post: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
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
                
                if (widget.post.idUsuario == 999) // Em um cenário real, compararíamos com o ID do usuário logado
                  PopupMenuButton<String>(
                    icon: const Icon(Icons.more_vert, color: Colors.grey),
                    onSelected: (valor) {
                      if (valor == 'apagar') {
                        _deletarPost();
                      }
                    },
                    itemBuilder: (context) => [
                      const PopupMenuItem(
                        value: 'apagar',
                        child: Row(
                          children: [
                            Icon(Icons.delete, color: Colors.red, size: 20),
                            SizedBox(width: 8),
                            Text('Apagar Publicação', style: TextStyle(color: Colors.red)),
                          ],
                        ),
                      ),
                    ],
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
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => PostDetailScreen(post: widget.post),
                        ),
                      ).then((_) {
                        if (widget.onPostDeletado != null) {
                           widget.onPostDeletado!();
                        }
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