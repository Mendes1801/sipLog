import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import '../models/user_models.dart';
import '../services/http_user_service.dart';
import '../services/http_api_service.dart';

class EditProfileScreen extends StatefulWidget {
  const EditProfileScreen({super.key});

  @override
  State<EditProfileScreen> createState() => _EditProfileScreenState();
}

class _EditProfileScreenState extends State<EditProfileScreen> {
  final _nomeController = TextEditingController();
  final _bioController = TextEditingController();
  final _senhaController = TextEditingController();

  String? _imagemPath; // Caminho local para nova imagem ou URL para imagem existente
  bool _isLocalImage = false;
  final ImagePicker _picker = ImagePicker();
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _carregarPerfil();
    });
  }

  Future<void> _carregarPerfil() async {
    final userService = Provider.of<HttpUserService>(context, listen: false);
    try {
      final perfil = await userService.getMeuPerfil();
      if (mounted) {
        setState(() {
          _nomeController.text = perfil.usuario?.nome ?? '';
          _bioController.text = perfil.usuario?.bio ?? '';
          _imagemPath = perfil.usuario?.fotoAvatarUrl;
          _isLocalImage = false;
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

  Future<void> _selecionarImagem() async {
    try {
      final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
      if (image != null) {
        setState(() {
          _imagemPath = image.path;
          _isLocalImage = true;
        });
      }
    } catch (e) {
      debugPrint('Erro ao selecionar imagem: $e');
    }
  }

  Future<void> _salvarPerfil() async {
    if (_nomeController.text.trim().isEmpty) return;

    setState(() => _carregando = true);
    final userService = Provider.of<HttpUserService>(context, listen: false);
    final apiService = Provider.of<HttpUserService>(context, listen: false); // HttpUserService herda de HttpApiService

    try {
      String? finalAvatarUrl = _isLocalImage ? null : _imagemPath;

      if (_isLocalImage && _imagemPath != null) {
        debugPrint('Fazendo upload do novo avatar...');
        finalAvatarUrl = await apiService.upload(_imagemPath!);
        debugPrint('Upload do avatar concluído: $finalAvatarUrl');
      }

      await userService.atualizarMeuPerfil(UsuarioUpdateDTO(
        nome: _nomeController.text,
        bio: _bioController.text,
        fotoAvatarUrl: finalAvatarUrl,
      ));
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Perfil atualizado com sucesso!'), backgroundColor: Colors.green),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao salvar perfil: $e')),
        );
      }
    }
  }

  void _mostrarDialogExclusao() {
    _senhaController.clear();
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Row(
            children: [
              Icon(Icons.warning_amber_rounded, color: Colors.red),
              SizedBox(width: 10),
              Text('Excluir Perfil'),
            ],
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Esta ação é irreversível. Todas as suas postagens serão apagadas.', style: TextStyle(fontSize: 14)),
              const SizedBox(height: 15),
              TextField(
                controller: _senhaController,
                obscureText: true,
                decoration: const InputDecoration(hintText: 'Sua senha', border: OutlineInputBorder(), prefixIcon: Icon(Icons.lock_outline)),
              ),
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancelar')),
            ElevatedButton(
              style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
              onPressed: () {
                if (_senhaController.text.isEmpty) return;
                Navigator.pop(context);
                Navigator.of(context).popUntil((route) => route.isFirst);
              },
              child: const Text('Excluir Definitivamente', style: TextStyle(color: Colors.white)),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Editar Perfil', style: TextStyle(fontFamily: 'BaksoSapi')),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: _carregando
        ? const Center(child: CircularProgressIndicator())
        : SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // ÁREA DE FOTO
            Center(
              child: Stack(
                children: [
                  CircleAvatar(
                    radius: 60,
                    backgroundColor: Colors.grey.shade200,
                    backgroundImage: _imagemPath != null && _imagemPath!.isNotEmpty
                        ? (_isLocalImage 
                            ? FileImage(File(_imagemPath!)) 
                            : NetworkImage(_imagemPath!) as ImageProvider)
                        : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                  ),
                  Positioned(
                    bottom: 0,
                    right: 0,
                    child: GestureDetector(
                      onTap: _selecionarImagem, // Retornado para a função direta
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: const BoxDecoration(color: Colors.deepPurple, shape: BoxShape.circle),
                        child: const Icon(Icons.camera_alt, color: Colors.white, size: 20),
                      ),
                    ),
                  )
                ],
              ),
            ),
            const SizedBox(height: 30),

            TextField(
              controller: _nomeController,
              decoration: const InputDecoration(labelText: 'Nome de exibição', border: OutlineInputBorder(), prefixIcon: Icon(Icons.person_outline)),
            ),
            const SizedBox(height: 20),

            TextField(
              controller: _bioController,
              maxLines: 3,
              decoration: const InputDecoration(labelText: 'Biografia', border: OutlineInputBorder(), alignLabelWithHint: true),
            ),
            const SizedBox(height: 30),

            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: _salvarPerfil,
                style: ElevatedButton.styleFrom(backgroundColor: Colors.deepPurple, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))),
                child: const Text('Salvar Alterações', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
              ),
            ),

            const SizedBox(height: 40),
            const Divider(),
            const SizedBox(height: 20),

            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Zona de Perigo', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold, fontSize: 16)),
            ),
            const SizedBox(height: 10),
            SizedBox(
              width: double.infinity,
              height: 50,
              child: OutlinedButton.icon(
                onPressed: _mostrarDialogExclusao,
                icon: const Icon(Icons.delete_forever, color: Colors.red),
                label: const Text('Apagar Perfil', style: TextStyle(color: Colors.red, fontSize: 16)),
                style: OutlinedButton.styleFrom(side: const BorderSide(color: Colors.red), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))),
              ),
            ),
          ],
        ),
      ),
    );
  }
}