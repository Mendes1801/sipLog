import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../services/mock_user_service.dart';

class EditProfileScreen extends StatefulWidget {
  const EditProfileScreen({super.key});

  @override
  State<EditProfileScreen> createState() => _EditProfileScreenState();
}

class _EditProfileScreenState extends State<EditProfileScreen> {
  // Inicializa os campos com o que está salvo no mock
  late final TextEditingController _nomeController;
  late final TextEditingController _bioController;
  final _senhaController = TextEditingController();

  String? _imagemPath;
  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    _nomeController = TextEditingController(text: MockUserService.nomeUsuario);
    _bioController = TextEditingController(text: MockUserService.bio);
    _imagemPath = MockUserService.fotoAvatarUrl;
  }

  Future<void> _selecionarImagem() async {
    try {
      // Pega a imagem direto da galeria e já exibe, sem passar pelo cropper
      final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
      
      if (image != null) {
        setState(() {
          _imagemPath = image.path;
        });
      }
    } catch (e) {
      debugPrint('Erro ao selecionar imagem: $e');
    }
  }

  void _salvarPerfil() {
    if (_nomeController.text.trim().isEmpty) return;

    // Salva no nosso "banco" temporário
    MockUserService.nomeUsuario = _nomeController.text;
    MockUserService.bio = _bioController.text;
    MockUserService.fotoAvatarUrl = _imagemPath;

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Perfil atualizado com sucesso!'), backgroundColor: Colors.green),
    );
    Navigator.pop(context); // Volta avisando a tela anterior
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
      body: SingleChildScrollView(
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
                    backgroundImage: _imagemPath != null 
                        ? NetworkImage(_imagemPath!) 
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