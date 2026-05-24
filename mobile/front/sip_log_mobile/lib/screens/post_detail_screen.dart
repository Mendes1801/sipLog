import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../models/experiencia_models.dart';
import '../services/http_experiencia_service.dart';
import '../widgets/post_card.dart';

class PostDetailScreen extends StatefulWidget {
  final FeedResponseModel post;

  const PostDetailScreen({super.key, required this.post});

  @override
  State<PostDetailScreen> createState() => _PostDetailScreenState();
}

class _PostDetailScreenState extends State<PostDetailScreen> {
  final TextEditingController _comentarioController = TextEditingController();
  
  List<ComentarioResponseDTO> _comentarios = [];
  FeedResponseModel? _postCompleto;
  bool _carregando = true;
  bool _enviando = false;

  @override
  void initState() {
    super.initState();
    _postCompleto = widget.post; // Inicia com o que temos do feed
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _carregarDadosIniciais();
    });
  }

  Future<void> _carregarDadosIniciais() async {
    final experienciaService = Provider.of<HttpExperienciaService>(context, listen: false);
    try {
      // Busca dados atualizados do post e a lista de comentários em paralelo
      final resultados = await Future.wait([
        experienciaService.buscarExperienciaPorId(widget.post.idPost),
        experienciaService.listarComentarios(widget.post.idPost),
      ]);

      if (mounted) {
        setState(() {
          _postCompleto = resultados[0] as FeedResponseModel;
          _comentarios = (resultados[1] as PaginaBffComentarioDTO).content;
          _carregando = false;
        });
      }
    } catch (e) {
      debugPrint('Erro ao carregar dados: $e');
      // Tenta carregar apenas comentários se o fetch do post falhar
      _carregarComentarios();
    }
  }

  Future<void> _carregarComentarios() async {
    final experienciaService = Provider.of<HttpExperienciaService>(context, listen: false);
    try {
      final pagina = await experienciaService.listarComentarios(widget.post.idPost);
      if (mounted) {
        setState(() {
          _comentarios = pagina.content;
          _carregando = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar detalhes: $e')),
        );
      }
    }
  }

  Future<void> _enviarComentario() async {
    if (_comentarioController.text.trim().isEmpty) return;
    
    setState(() => _enviando = true);
    final experienciaService = Provider.of<HttpExperienciaService>(context, listen: false);

    try {
      final novo = await experienciaService.adicionarComentario(
        widget.post.idPost,
        NovoComentarioDTO(texto: _comentarioController.text)
      );

      if (mounted) {
        setState(() {
          _comentarios.insert(0, novo);
          _comentarioController.clear();
          _enviando = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _enviando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao enviar comentário: $e')),
        );
      }
    }
    
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
                if (_postCompleto != null)
                  PostCard(
                    post: _postCompleto!,
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
                      backgroundImage: c.autor?.fotoAvatarUrl != null
                        ? NetworkImage(c.autor!.fotoAvatarUrl!)
                        : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                    ),
                    title: Row(
                      children: [
                        Text(c.autor?.nome ?? 'Anônimo', style: const TextStyle(fontWeight: FontWeight.bold)),
                        const SizedBox(width: 8),
                        Text(c.tempoDecorrido ?? '', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                      ],
                    ),
                    subtitle: Text(c.texto ?? ''),
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
