import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../models/user_models.dart';
import '../services/auth_service.dart';
import '../services/http_user_service.dart';
import '../services/http_feed_service.dart';
import 'post_detail_screen.dart';
import 'user_list_screen.dart';

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
  bool _estaSeguindo = false;

  @override
  void initState() {
    super.initState();
    _carregarDados();
  }

  Future<void> _carregarDados() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    final feedService = Provider.of<HttpFeedService>(context, listen: false);

    try {
      debugPrint('🔍 Buscando perfil do usuário: ${widget.idUsuario}');
      final perfil = await userService.getPerfilUsuario(widget.idUsuario);
      
      debugPrint('✅ Perfil recebido: ${perfil.usuario?.nome}');
      final posts = await feedService.getFeedDeUsuario(widget.idUsuario);
      
      if (mounted) {
        setState(() {
          _perfil = perfil;
          _posts = posts;
          _estaSeguindo = perfil.seguindoPorMim ?? false;
          _carregando = false;
        });
      }
    } catch (e) {
      debugPrint('❌ Erro ao carregar perfil: $e');
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
    
    // Feedback imediato
    setState(() => _estaSeguindo = !_estaSeguindo);
    
    try {
      await userService.alternarSeguir(widget.idUsuario);
      // Recarrega os dados para atualizar contadores e confirmar estado
      _carregarDados();
    } catch (e) {
      // Reverte se falhar
      setState(() => _estaSeguindo = !_estaSeguindo);
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
        backgroundColor: Colors.transparent,
        elevation: 0,
        foregroundColor: Theme.of(context).colorScheme.primary,
      ),
      body: _carregando
          ? Center(child: CircularProgressIndicator(color: Theme.of(context).colorScheme.primary))
          : RefreshIndicator(
              onRefresh: _carregarDados,
              color: Theme.of(context).colorScheme.primary,
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
                              GestureDetector(
                                onTap: () {
                                  if (_perfil?.usuario?.idUsuario != null) {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (context) => UserListScreen(
                                          idUsuario: _perfil!.usuario!.idUsuario!,
                                          nomeUsuario: _perfil!.usuario!.nome ?? 'Usuário',
                                          modo: UserListMode.seguidores,
                                        ),
                                      ),
                                    );
                                  }
                                },
                                child: _buildEstatistica('Seguidores', (_perfil?.estatisticas?.seguidores ?? 0).toString()),
                              ),
                              GestureDetector(
                                onTap: () {
                                  if (_perfil?.usuario?.idUsuario != null) {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (context) => UserListScreen(
                                          idUsuario: _perfil!.usuario!.idUsuario!,
                                          nomeUsuario: _perfil!.usuario!.nome ?? 'Usuário',
                                          modo: UserListMode.seguindo,
                                        ),
                                      ),
                                    );
                                  }
                                },
                                child: _buildEstatistica('Seguindo', (_perfil?.estatisticas?.seguindo ?? 0).toString()),
                              ),
                            ],
                          ),
                          const SizedBox(height: 20),
                          
                          Consumer<AuthService>(
                            builder: (context, auth, _) {
                              if (auth.userId == widget.idUsuario) return const SizedBox.shrink();
                              
                              return SizedBox(
                                width: double.infinity,
                                child: AnimatedSwitcher(
                                  duration: const Duration(milliseconds: 300),
                                  child: ElevatedButton(
                                    key: ValueKey(_estaSeguindo),
                                    onPressed: _alternarSeguir,
                                    style: ElevatedButton.styleFrom(
                                      backgroundColor: _estaSeguindo ? Colors.transparent : Theme.of(context).colorScheme.primary,
                                      foregroundColor: _estaSeguindo ? Theme.of(context).colorScheme.primary : Theme.of(context).colorScheme.onPrimary,
                                      side: BorderSide(color: Theme.of(context).colorScheme.primary),
                                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                    ),
                                    child: Text(_estaSeguindo ? 'Seguindo' : 'Seguir'),
                                  ),
                                ),
                              );
                            },
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
        Text(valor, style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Theme.of(context).colorScheme.primary)),
        Text(titulo, style: const TextStyle(color: Colors.grey, fontSize: 13)),
      ],
    );
  }
}
