import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/bebida_models.dart';
import '../services/http_bebida_service.dart';

class NovaBebidaScreen extends StatefulWidget {
  const NovaBebidaScreen({super.key});

  @override
  State<NovaBebidaScreen> createState() => _NovaBebidaScreenState();
}

class _NovaBebidaScreenState extends State<NovaBebidaScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nomeController = TextEditingController();
  final _fabricanteController = TextEditingController();
  final _categoriaController = TextEditingController();
  
  final List<MapEntry<TextEditingController, TextEditingController>> _caracteristicasControllers = [];

  void _addCaracteristica() {
    setState(() {
      _caracteristicasControllers.add(MapEntry(TextEditingController(), TextEditingController()));
    });
  }

  void _removeCaracteristica(int index) {
    setState(() {
      _caracteristicasControllers[index].key.dispose();
      _caracteristicasControllers[index].value.dispose();
      _caracteristicasControllers.removeAt(index);
    });
  }

  Future<void> _salvar() async {
    if (!_formKey.currentState!.validate()) return;

    final bebidaService = Provider.of<HttpBebidaService>(context, listen: false);

    Map<String, String> caracteristicas = {};
    for (var entry in _caracteristicasControllers) {
      if (entry.key.text.isNotEmpty) {
        caracteristicas[entry.key.text] = entry.value.text;
      }
    }

    try {
      final novaBebida = await bebidaService.adicionarBebida(NovaBebidaDTO(
        nome: _nomeController.text,
        fabricante: _fabricanteController.text,
        categoria: _categoriaController.text,
        caracteristicas: caracteristicas,
      ));

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Bebida cadastrada com sucesso!')),
        );
        Navigator.pop(context, novaBebida);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao cadastrar bebida: $e')),
        );
      }
    }
  }

  @override
  void dispose() {
    _nomeController.dispose();
    _fabricanteController.dispose();
    _categoriaController.dispose();
    for (var entry in _caracteristicasControllers) {
      entry.key.dispose();
      entry.value.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Cadastrar Bebida', style: TextStyle(fontFamily: 'BaksoSapi')),
        backgroundColor: Colors.deepPurple,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextFormField(
                controller: _nomeController,
                decoration: const InputDecoration(labelText: 'Nome da Bebida', border: OutlineInputBorder()),
                validator: (v) => v == null || v.isEmpty ? 'Campo obrigatório' : null,
              ),
              const SizedBox(height: 15),
              TextFormField(
                controller: _fabricanteController,
                decoration: const InputDecoration(labelText: 'Fabricante', border: OutlineInputBorder()),
                validator: (v) => v == null || v.isEmpty ? 'Campo obrigatório' : null,
              ),
              const SizedBox(height: 15),
              TextFormField(
                controller: _categoriaController,
                decoration: const InputDecoration(labelText: 'Categoria (ex: Cerveja, Vinho)', border: OutlineInputBorder()),
                validator: (v) => v == null || v.isEmpty ? 'Campo obrigatório' : null,
              ),
              const SizedBox(height: 30),
              const Text('Características Adicionais:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              const SizedBox(height: 10),
              ...List.generate(_caracteristicasControllers.length, (index) {
                return Padding(
                  padding: const EdgeInsets.only(bottom: 10),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextFormField(
                          controller: _caracteristicasControllers[index].key,
                          decoration: const InputDecoration(hintText: 'Chave (ex: Teor Alcoólico)', border: OutlineInputBorder()),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: TextFormField(
                          controller: _caracteristicasControllers[index].value,
                          decoration: const InputDecoration(hintText: 'Valor (ex: 4.5%)', border: OutlineInputBorder()),
                        ),
                      ),
                      IconButton(
                        onPressed: () => _removeCaracteristica(index),
                        icon: const Icon(Icons.remove_circle, color: Colors.red),
                      ),
                    ],
                  ),
                );
              }),
              TextButton.icon(
                onPressed: _addCaracteristica,
                icon: const Icon(Icons.add),
                label: const Text('Adicionar Característica'),
              ),
              const SizedBox(height: 40),
              SizedBox(
                width: double.infinity,
                height: 50,
                child: ElevatedButton(
                  onPressed: _salvar,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.deepPurple,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))
                  ),
                  child: const Text('Salvar Bebida', style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
