import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/user_models.dart';
import '../services/http_user_service.dart';
import 'user_profile_screen.dart';

enum UserListMode { seguidores, seguindo }

class UserListScreen extends StatefulWidget {
  final int idUsuario;
  final String nomeUsuario;
  final UserListMode modo;

  const UserListScreen({
    super.key,
    required this.idUsuario,
    required this.nomeUsuario,
    required this.modo,
  });

  @override
  State<UserListScreen> createState() => _UserListScreenState();
}

class _UserListScreenState extends State<UserListScreen> {
  List<UsuarioResumoDTO> _usuarios = [];
  bool _carregando = true;
  int _paginaAtual = 0;
  bool _temMais = true;
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _carregarUsuarios();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >= _scrollController.position.maxScrollExtent - 200 &&
        !_carregando &&
        _temMais) {
      _carregarUsuarios();
    }
  }

  Future<void> _carregarUsuarios() async {
    if (!_temMais) return;

    setState(() => _carregando = true);
    final userService = Provider.of<HttpUserService>(context, listen: false);

    try {
      List<UsuarioResumoDTO> novosUsuarios;
      if (widget.modo == UserListMode.seguidores) {
        novosUsuarios = await userService.getSeguidores(widget.idUsuario, pagina: _paginaAtual);
      } else {
        novosUsuarios = await userService.getSeguindo(widget.idUsuario, pagina: _paginaAtual);
      }

      if (mounted) {
        setState(() {
          _usuarios.addAll(novosUsuarios);
          _carregando = false;
          _paginaAtual++;
          if (novosUsuarios.length < 20) { // Assumindo tamanho de página padrão do Spring Data
            _temMais = false;
          }
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar lista: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final titulo = widget.modo == UserListMode.seguidores ? 'Seguidores' : 'Seguindo';

    return Scaffold(
      appBar: AppBar(
        title: Text('$titulo - ${widget.nomeUsuario}', style: const TextStyle(fontSize: 18)),
      ),
      body: _usuarios.isEmpty && !_carregando
          ? Center(child: Text(widget.modo == UserListMode.seguidores ? 'Nenhum seguidor ainda.' : 'Não segue ninguém ainda.'))
          : ListView.separated(
              controller: _scrollController,
              itemCount: _usuarios.length + (_temMais ? 1 : 0),
              separatorBuilder: (context, index) => const Divider(height: 1),
              itemBuilder: (context, index) {
                if (index == _usuarios.length) {
                  return const Padding(
                    padding: EdgeInsets.all(15.0),
                    child: Center(child: CircularProgressIndicator()),
                  );
                }

                final usuario = _usuarios[index];
                return ListTile(
                  leading: CircleAvatar(
                    backgroundImage: usuario.fotoAvatarUrl != null && usuario.fotoAvatarUrl!.isNotEmpty
                        ? NetworkImage(usuario.fotoAvatarUrl!)
                        : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                  ),
                  title: Text(usuario.nome ?? 'Sem nome', style: const TextStyle(fontWeight: FontWeight.bold)),
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
    );
  }
}
