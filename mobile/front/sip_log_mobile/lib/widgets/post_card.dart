import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../models/feed_response_model.dart';

class PostCard extends StatefulWidget {
  final FeedResponseModel post;

  const PostCard({super.key, required this.post});

  @override
  State<PostCard> createState() => _PostCardState();
}

class _PostCardState extends State<PostCard> {
  // Variáveis de estado local para controlar a animação imediata
  late bool _curtido;
  late int _totalCurtidas;

  @override
  void initState() {
    super.initState();
    // Inicializa com os dados que vieram da API/Mock
    _curtido = widget.post.curtidoPorMim;
    _totalCurtidas = widget.post.totalCurtidas;
  }

  // Função disparada ao clicar no coração
  Future<void> _alternarCurtida() async {
    // 1. Atualiza a interface instantaneamente (Otimismo de UI)
    setState(() {
      _curtido = !_curtido;
      _totalCurtidas += _curtido ? 1 : -1;
    });

    // 2. Tenta bater no Backend real na porta 8081
    try {
      final url = Uri.parse('http://localhost:8081/api/v1/experiencias/${widget.post.idPost}/curtir');
      final response = await http.post(url);

      if (response.statusCode != 200) {
        // Se o Spring Boot retornar erro (ex: 403, 500), desfazemos a animação silenciosamente
        setState(() {
          _curtido = !_curtido;
          _totalCurtidas += _curtido ? 1 : -1;
        });
      }
    } catch (e) {
      // Se der erro de rede (ex: backend desligado), desfazemos também
      setState(() {
        _curtido = !_curtido;
        _totalCurtidas += _curtido ? 1 : -1;
      });
      
      setState(() {
         _curtido = !_curtido;
         _totalCurtidas += _curtido ? 1 : -1;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
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
                // Transformamos o botão de curtir
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
                Row(
                  children: [
                    const Icon(Icons.chat_bubble_outline, color: Colors.grey),
                    const SizedBox(width: 5),
                    Text('${widget.post.totalComentarios} Comentários'),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}