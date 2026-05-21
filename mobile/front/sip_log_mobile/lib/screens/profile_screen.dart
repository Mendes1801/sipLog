import 'package:flutter/material.dart';
import '../models/feed_response_model.dart';
import '../services/mock_feed_service.dart';
import '../services/mock_user_service.dart'; // Import do nosso novo "Banco"
import 'post_detail_screen.dart';
import 'edit_profile_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final MockFeedService _feedService = MockFeedService();
  List<FeedResponseModel> _meusPosts = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    _carregarMeusPosts();
  }

  Future<void> _carregarMeusPosts() async {
    final todosPosts = await _feedService.getFeedGlobal();
    setState(() {
      _meusPosts = todosPosts.where((post) => post.idUsuario == 999).toList();
      _carregando = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return _carregando
        ? const Center(child: CircularProgressIndicator(color: Colors.deepPurple))
        : RefreshIndicator(
            onRefresh: _carregarMeusPosts,
            color: Colors.deepPurple,
            child: CustomScrollView(
              slivers: [
                // CABEÇALHO DO PERFIL
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: Column(
                      children: [
                        CircleAvatar(
                          radius: 50,
                          backgroundColor: Colors.grey.shade300,
                          // LÊ A FOTO SALVA NO MOCK
                          backgroundImage: MockUserService.fotoAvatarUrl != null
                              ? NetworkImage(MockUserService.fotoAvatarUrl!)
                              : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                        ),
                        const SizedBox(height: 15),
                        Text(
                          MockUserService.nomeUsuario, // LÊ O NOME DO MOCK
                          style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 5),
                        Text(
                          MockUserService.bio, // LÊ A BIO DO MOCK
                          textAlign: TextAlign.center,
                          style: const TextStyle(color: Colors.grey, fontSize: 14),
                        ),
                        const SizedBox(height: 20),
                        
                        // ESTATÍSTICAS
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            _buildEstatistica('Degustações', _meusPosts.length.toString()),
                            _buildEstatistica('Seguidores', '12'),
                            _buildEstatistica('Seguindo', '28'),
                          ],
                        ),
                        const SizedBox(height: 20),
                        
                        // BOTÃO DE EDITAR PERFIL
                        SizedBox(
                          width: double.infinity,
                          child: OutlinedButton(
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (context) => const EditProfileScreen()),
                              ).then((_) {
                                // QUANDO VOLTAR DA EDIÇÃO, ATUALIZA A TELA!
                                setState(() {});
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

                // GRADE DE PUBLICAÇÕES ESTILO INSTAGRAM
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
                      childAspectRatio: 1.0, // Força itens quadrados
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
                            ).then((_) => _carregarMeusPosts());
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