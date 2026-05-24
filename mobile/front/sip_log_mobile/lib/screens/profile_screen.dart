import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../models/user_models.dart';
import '../services/http_feed_service.dart';
import '../services/http_user_service.dart';
import 'post_detail_screen.dart';
import 'edit_profile_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  PerfilDTO? _perfil;
  List<FeedResponseModel> _meusPosts = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _carregarDados();
    });
  }

  Future<void> _carregarDados() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    final feedService = Provider.of<HttpFeedService>(context, listen: false);

    try {
      final perfil = await userService.getMeuPerfil();
      final posts = await feedService.getFeedMe();
      if (mounted) {
        setState(() {
          _perfil = perfil;
          _meusPosts = posts;
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

  @override
  Widget build(BuildContext context) {
    return _carregando
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
                          child: OutlinedButton(
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (context) => const EditProfileScreen()),
                              ).then((_) {
                                _carregarDados();
                              });
                            },
                            style: OutlinedButton.styleFrom(
                              side: const BorderSide(color: Colors.deepPurple),
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                            ),
                            child: const Text('Editar Perfil', style: TextStyle(color: Colors.deepPurple)),
                          ),
                        ),
                        const SizedBox(height: 20),
                        const Divider(height: 1),
                      ],
                    ),
                  ),
                ),

                if (_meusPosts.isEmpty)
                  const SliverToBoxAdapter(
                    child: Padding(
                      padding: EdgeInsets.all(40.0),
                      child: Center(
                        child: Text(
                          'Você ainda não registrou nenhuma experiência.',
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
                        final post = _meusPosts[index];
                        return GestureDetector(
                          onTap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => PostDetailScreen(post: post),
                              ),
                            ).then((_) => _carregarDados());
                          },
                          child: ClipRect(
                            child: Stack(
                              fit: StackFit.expand,
                              children: [
                                Image.network(
                                  post.fotoPostUrl ?? 'https://via.placeholder.com/150',
                                  fit: BoxFit.cover,
                                ),
                                Positioned(
                                  bottom: 4,
                                  right: 4,
                                  child: Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                                    decoration: BoxDecoration(
                                      color: Colors.black.withOpacity(0.7),
                                      borderRadius: BorderRadius.circular(4),
                                    ),
                                    child: Row(
                                      children: [
                                        const Icon(Icons.star, color: Colors.amber, size: 12),
                                        const SizedBox(width: 2),
                                        Text(
                                          post.nota.toString(),
                                          style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                      childCount: _meusPosts.length,
                    ),
                  ),
              ],
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
