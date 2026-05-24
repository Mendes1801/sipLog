import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/user_models.dart';
import '../services/http_user_service.dart';

class SeguindoScreen extends StatefulWidget {
  final int idUsuario;
  final String nomeUsuario;

  const SeguindoScreen({super.key, required this.idUsuario, required this.nomeUsuario});

  @override
  State<SeguindoScreen> createState() => _SeguindoScreenState();
}

class _SeguindoScreenState extends State<SeguindoScreen> {
  List<UsuarioResumoDTO> _seguindo = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    _carregarSeguindo();
  }

  Future<void> _carregarSeguindo() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    try {
      final lista = await userService.getSeguindo(widget.idUsuario);
      if (mounted) {
        setState(() {
          _seguindo = lista;
          _carregando = false;
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
    return Scaffold(
      appBar: AppBar(
        title: Text('Seguindo - ${widget.nomeUsuario}', style: const TextStyle(fontSize: 18)),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: _carregando
          ? const Center(child: CircularProgressIndicator(color: Colors.deepPurple))
          : _seguindo.isEmpty
              ? const Center(child: Text('Não segue ninguém ainda.'))
              : ListView.separated(
                  itemCount: _seguindo.length,
                  separatorBuilder: (context, index) => const Divider(height: 1),
                  itemBuilder: (context, index) {
                    final usuario = _seguindo[index];
                    return ListTile(
                      leading: CircleAvatar(
                        backgroundImage: usuario.fotoAvatarUrl != null
                            ? NetworkImage(usuario.fotoAvatarUrl!)
                            : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                      ),
                      title: Text(usuario.nome ?? 'Sem nome', style: const TextStyle(fontWeight: FontWeight.bold)),
                      onTap: () {
                        // Navegar para perfil do usuário se necessário
                      },
                    );
                  },
                ),
    );
  }
}
