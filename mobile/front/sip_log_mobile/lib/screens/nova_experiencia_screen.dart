import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart'; 
import 'package:provider/provider.dart';
import '../models/bebida_models.dart';
import '../models/experiencia_models.dart';
import '../services/http_bebida_service.dart';
import '../services/http_experiencia_service.dart';
import 'nova_bebida_screen.dart';

class NovaExperienciaScreen extends StatefulWidget {
  const NovaExperienciaScreen({super.key});

  @override
  State<NovaExperienciaScreen> createState() => _NovaExperienciaScreenState();
}

class _NovaExperienciaScreenState extends State<NovaExperienciaScreen> {
  final _comentarioController = TextEditingController();
  double _nota = 5.0;
  String _visibilidade = 'PUBLICA';
  
  XFile? _imagemSelecionada;
  final ImagePicker _picker = ImagePicker();

  List<BebidaResumoDTO> _bebidasDisponiveis = [];
  BebidaResumoDTO? _bebidaSelecionada;
  bool _carregandoBebidas = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _carregarBebidas();
    });
  }

  Future<void> _carregarBebidas() async {
    final bebidaService = Provider.of<HttpBebidaService>(context, listen: false);
    try {
      debugPrint('Buscando bebidas...');
      final bebidas = await bebidaService.buscarBebidas("");
      debugPrint('Bebidas encontradas: ${bebidas.length}');
      if (mounted) {
        setState(() {
          _bebidasDisponiveis = bebidas;
          _carregandoBebidas = false;
        });
      }
    } catch (e) {
      debugPrint('Erro ao carregar bebidas: $e');
      if (mounted) {
        setState(() => _carregandoBebidas = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar bebidas: $e')),
        );
      }
    }
  }

  Future<void> _selecionarImagem() async {
    try {
      final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
      if (image != null) {
        setState(() {
          _imagemSelecionada = image; 
        });
      }
    } catch (e) {
      debugPrint('Erro ao selecionar imagem: $e');
    }
  }

  Future<void> _postar() async {
    if (_bebidaSelecionada == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Por favor, selecione uma bebida!')),
      );
      return;
    }

    if (_imagemSelecionada == null) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Por favor, selecione uma foto!')),
        );
        return;
    }

    setState(() => _carregandoBebidas = true); // Usando flag de busy reutilizada

    final experienciaService = Provider.of<HttpExperienciaService>(context, listen: false);

    try {
      // 1. Upload da foto primeiro
      debugPrint('Fazendo upload da foto...');
      final String urlFoto = await experienciaService.upload(_imagemSelecionada!.path);
      debugPrint('Upload concluído: $urlFoto');

      // 2. Registrar a experiência com a URL retornada
      await experienciaService.registrarNovaExperiencia(NovaExperienciaDTO(
        idBebida: _bebidaSelecionada!.idBebida!,
        nota: _nota,
        comentario: _comentarioController.text,
        visibilidade: _visibilidade,
        fotoPostUrl: urlFoto,
        localizacao: 'Minha Localização',
      ));
      if (mounted) {
        Navigator.pop(context, true);
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregandoBebidas = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao publicar: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Nova Experiência', style: TextStyle(fontFamily: 'BaksoSapi')),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            GestureDetector(
              onTap: _selecionarImagem,
              child: Container(
                height: 200,
                width: double.infinity,
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(15),
                  border: Border.all(color: Colors.deepPurple.withOpacity(0.3)),
                ),
                child: _imagemSelecionada != null
                    ? ClipRRect(
                        borderRadius: BorderRadius.circular(15),
                        child: Image.file(File(_imagemSelecionada!.path), fit: BoxFit.cover, width: double.infinity,),
                      )
                    : Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.add_a_photo, size: 60, color: Colors.deepPurple),
                          const SizedBox(height: 10),
                          const Text('Toque para adicionar foto', style: TextStyle(color: Colors.deepPurple, fontWeight: FontWeight.bold)),
                        ],
                      ),
              ),
            ),
            const SizedBox(height: 30),

            _carregandoBebidas
              ? const CircularProgressIndicator()
              : Row(
                  children: [
                    Expanded(
                      child: DropdownButtonFormField<BebidaResumoDTO>(
                        decoration: const InputDecoration(
                          labelText: 'Qual bebida você escolheu?',
                          border: OutlineInputBorder(),
                        ),
                        value: _bebidaSelecionada,
                        items: _bebidasDisponiveis.map((bebida) {
                          return DropdownMenuItem<BebidaResumoDTO>(
                            value: bebida,
                            child: Text(bebida.nome ?? 'Sem nome'),
                          );
                        }).toList(),
                        onChanged: (valor) {
                          setState(() {
                            _bebidaSelecionada = valor;
                          });
                        },
                      ),
                    ),
                    const SizedBox(width: 10),
                    IconButton(
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (context) => const NovaBebidaScreen()),
                        ).then((bebidaCadastrada) {
                          if (bebidaCadastrada != null && bebidaCadastrada is BebidaResumoDTO) {
                            setState(() {
                              _bebidasDisponiveis.add(bebidaCadastrada);
                              _bebidaSelecionada = bebidaCadastrada;
                            });
                          } else {
                            _carregarBebidas();
                          }
                        });
                      },
                      icon: const Icon(Icons.add_circle, color: Colors.deepPurple, size: 40),
                      tooltip: 'Cadastrar nova bebida',
                    ),
                  ],
                ),
            const SizedBox(height: 20),

            const Text('Sua Nota:', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            Slider(
              value: _nota,
              min: 0,
              max: 5,
              divisions: 10,
              label: _nota.toStringAsFixed(1),
              onChanged: (v) => setState(() => _nota = v),
              activeColor: Colors.amber,
            ),

            TextField(
              controller: _comentarioController,
              maxLines: 3,
              decoration: const InputDecoration(
                labelText: 'Comentário (opcional)',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 20),

            DropdownButtonFormField<String>(
              decoration: const InputDecoration(
                labelText: 'Quem pode ver?',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.visibility),
              ),
              value: _visibilidade,
              items: const [
                DropdownMenuItem(value: 'PUBLICA', child: Text('Pública (Todos)')),
                DropdownMenuItem(value: 'AMIGOS', child: Text('Amigos')),
                DropdownMenuItem(value: 'PRIVADA', child: Text('Privada (Só eu)')),
              ],
              onChanged: (v) {
                if (v != null) setState(() => _visibilidade = v);
              },
            ),
            const SizedBox(height: 30),

            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: _postar,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))
                ),
                child: const Text('Publicar no Feed', style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
