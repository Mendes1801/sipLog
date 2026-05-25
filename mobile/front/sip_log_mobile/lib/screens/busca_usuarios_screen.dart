import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'user_profile_screen.dart';
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
              ? Center(child: CircularProgressIndicator(color: Theme.of(context).colorScheme.primary))
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
                          trailing: const Icon(Icons.chevron_right, color: Colors.grey),
                          onTap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => UserProfileScreen(
                                  idUsuario: usuario.id!,
                                  nomeUsuario: usuario.nome,
                                ),
                              ),
                            );
                          },
                        );
                      },
                    ),
        ),
      ],
    );
  }
}
