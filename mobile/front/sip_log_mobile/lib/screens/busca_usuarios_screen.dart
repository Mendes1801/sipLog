import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/user_models.dart';
import '../services/http_user_service.dart';

class BuscaUsuariosScreen extends StatefulWidget {
  const BuscaUsuariosScreen({super.key});

  @override
  State<BuscaUsuariosScreen> createState() => _BuscaUsuariosScreenState();
}

class _BuscaUsuariosScreenState extends State<BuscaUsuariosScreen> {
  final _searchController = TextEditingController();
  List<UsuarioResumoDTO> _usuarios = [];
  bool _carregando = false;

  Future<void> _pesquisar(String query) async {
    if (query.isEmpty) {
      setState(() => _usuarios = []);
      return;
    }

    setState(() => _carregando = true);
    final userService = Provider.of<HttpUserService>(context, listen: false);

    try {
      final resultados = await userService.buscarUsuarios(query);
      if (mounted) {
        setState(() {
          _usuarios = resultados;
          _carregando = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro na busca: $e')),
        );
      }
    }
  }

  Future<void> _alternarSeguir(UsuarioResumoDTO usuario) async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    try {
      await userService.alternarSeguir(usuario.id!);
      // Em um cenário real, o backend retornaria se agora estamos seguindo ou não.
      // Como não temos esse dado no DTO de resumo, apenas notificamos o usuário.
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Ação realizada para o usuário ${usuario.nome}')),
        );
      }
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
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(15.0),
          child: TextField(
            controller: _searchController,
            decoration: InputDecoration(
              hintText: 'Pesquisar pessoas...',
              prefixIcon: const Icon(Icons.search),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(30)),
              suffixIcon: IconButton(
                icon: const Icon(Icons.clear),
                onPressed: () {
                  _searchController.clear();
                  _pesquisar("");
                },
              ),
            ),
            onChanged: (value) => _pesquisar(value),
          ),
        ),
        Expanded(
          child: _carregando
              ? const Center(child: CircularProgressIndicator(color: Colors.deepPurple))
              : _usuarios.isEmpty
                  ? Center(
                      child: Text(
                        _searchController.text.isEmpty 
                            ? 'Digite um nome para começar' 
                            : 'Nenhum usuário encontrado.',
                        style: const TextStyle(color: Colors.grey),
                      ),
                    )
                  : ListView.separated(
                      itemCount: _usuarios.length,
                      separatorBuilder: (context, index) => const Divider(height: 1),
                      itemBuilder: (context, index) {
                        final usuario = _usuarios[index];
                        return ListTile(
                          leading: CircleAvatar(
                            backgroundImage: usuario.fotoAvatarUrl != null
                                ? NetworkImage(usuario.fotoAvatarUrl!)
                                : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                          ),
                          title: Text(usuario.nome ?? 'Sem nome', style: const TextStyle(fontWeight: FontWeight.bold)),
                          trailing: ElevatedButton(
                            onPressed: () => _alternarSeguir(usuario),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.deepPurple,
                              foregroundColor: Colors.white,
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                            ),
                            child: const Text('Seguir'),
                          ),
                          onTap: () {
                            // Poderíamos navegar para o perfil do usuário aqui
                          },
                        );
                      },
                    ),
        ),
      ],
    );
  }
}
