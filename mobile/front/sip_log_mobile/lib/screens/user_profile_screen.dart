import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../models/user_models.dart';
import '../services/http_feed_service.dart';
import '../services/http_user_service.dart';
import 'post_detail_screen.dart';

class UserProfileScreen extends StatefulWidget {
  final int idUsuario;
  final String? nomeUsuario;

  const UserProfileScreen({super.key, required this.idUsuario, this.nomeUsuario});

  @override
  State<UserProfileScreen> createState() => _UserProfileScreenState();
}

class _UserProfileScreenState extends State<UserProfileScreen> {
  PerfilDTO? _perfil;
  List<FeedResponseModel> _posts = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    _carregarDados();
  }

  Future<void> _carregarDados() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    final feedService = Provider.of<HttpFeedService>(context, listen: false);

    try {
      final perfil = await userService.getPerfilUsuario(widget.idUsuario);
      final posts = await feedService.getFeedDeUsuario(widget.idUsuario);
      if (mounted) {
        setState(() {
          _perfil = perfil;
          _posts = posts;
          _carregando = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar perfil: $e')),
        );
      }
    }
  }

  Future<void> _alternarSeguir() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    try {
      await userService.alternarSeguir(widget.idUsuario);
      _carregarDados(); // Recarrega para atualizar contador de seguidores
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao processar: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.nomeUsuario ?? 'Perfil'),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: _carregando
          ? const Center(child: CircularProgressIndicator(color: Colors.deepPurple))
          : RefreshIndicator(
              onRefresh: _carregarDados,
              color: Colors.deepPurple,
              child: CustomScrollView(
                slivers: [
                  SliverToBoxAdapter(
                    child: Padding(
                      padding: const EdgeInsets.all(20.0),
                      child: Column(
                        children: [
                          CircleAvatar(
                            radius: 50,
                            backgroundColor: Colors.grey.shade300,
                            backgroundImage: _perfil?.usuario?.fotoAvatarUrl != null
                                ? NetworkImage(_perfil!.usuario!.fotoAvatarUrl!)
                                : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                          ),
                          const SizedBox(height: 15),
                          Text(
                            _perfil?.usuario?.nome ?? 'Usuário',
                            style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(height: 5),
                          Text(
                            _perfil?.usuario?.bio ?? 'Sem bio disponível.',
                            textAlign: TextAlign.center,
                            style: const TextStyle(color: Colors.grey, fontSize: 14),
                          ),
                          const SizedBox(height: 20),
                          
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              _buildEstatistica('Degustações', (_perfil?.estatisticas?.totalDegustacoes ?? 0).toString()),
                              _buildEstatistica('Seguidores', (_perfil?.estatisticas?.seguidores ?? 0).toString()),
                              _buildEstatistica('Seguindo', (_perfil?.estatisticas?.seguindo ?? 0).toString()),
                            ],
                          ),
                          const SizedBox(height: 20),
                          
                          SizedBox(
                            width: double.infinity,
                            child: ElevatedButton(
                              onPressed: _alternarSeguir,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.deepPurple,
                                foregroundColor: Colors.white,
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                              ),
                              child: const Text('Seguir / Parar de Seguir'),
                            ),
                          ),
                          const SizedBox(height: 20),
                          const Divider(height: 1),
                        ],
                      ),
                    ),
                  ),

                  if (_posts.isEmpty)
                    const SliverToBoxAdapter(
                      child: Padding(
                        padding: EdgeInsets.all(40.0),
                        child: Center(
                          child: Text(
                            'Este usuário ainda não registrou experiências.',
                            textAlign: TextAlign.center,
                            style: TextStyle(color: Colors.grey, fontSize: 16),
                          ),
                        ),
                      ),
                    )
                  else
                    SliverGrid(
                      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 3, 
                        crossAxisSpacing: 2,
                        mainAxisSpacing: 2,
                        childAspectRatio: 1.0,
                      ),
                      delegate: SliverChildBuilderDelegate(
                        (context, index) {
                          final post = _posts[index];
                          return GestureDetector(
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => PostDetailScreen(post: post),
                                ),
                              ).then((_) => _carregarDados());
                            },
                            child: Image.network(
                              post.fotoPostUrl ?? 'https://via.placeholder.com/150',
                              fit: BoxFit.cover,
                            ),
                          );
                        },
                        childCount: _posts.length,
                      ),
                    ),
                ],
              ),
            ),
    );
  }

  Widget _buildEstatistica(String titulo, String valor) {
    return Column(
      children: [
        Text(valor, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.deepPurple)),
        Text(titulo, style: const TextStyle(color: Colors.grey, fontSize: 13)),
      ],
    );
  }
}
