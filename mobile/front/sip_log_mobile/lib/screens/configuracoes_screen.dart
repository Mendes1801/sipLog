import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/theme_service.dart';

class ConfiguracoesScreen extends StatelessWidget {
  const ConfiguracoesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final themeService = Provider.of<ThemeService>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Configurações', style: TextStyle(fontFamily: 'BaksoSapi')),
      ),
      body: ListView(
        children: [
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              'Aparência',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.grey),
            ),
          ),
          SwitchListTile(
            title: const Text('Modo Escuro'),
            subtitle: const Text('Reduz o cansaço visual em ambientes escuros'),
            secondary: const Icon(Icons.dark_mode),
            value: themeService.themeMode == ThemeMode.dark,
            onChanged: (value) => themeService.toggleThemeMode(value),
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              'Acessibilidade (Cores)',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.grey),
            ),
          ),
          RadioListTile<ColorBlindnessType>(
            title: const Text('Padrão (SipLog Purple)'),
            value: ColorBlindnessType.none,
            groupValue: themeService.colorBlindnessType,
            onChanged: (v) => themeService.setColorBlindnessType(v!),
            secondary: Container(width: 24, height: 24, color: const Color(0xFF673AB7)),
          ),
          RadioListTile<ColorBlindnessType>(
            title: const Text('Protanopia (Azul/Amarelo)'),
            subtitle: const Text('Otimizado para daltonismo de vermelho'),
            value: ColorBlindnessType.protanopia,
            groupValue: themeService.colorBlindnessType,
            onChanged: (v) => themeService.setColorBlindnessType(v!),
            secondary: Container(width: 24, height: 24, color: const Color(0xFF0055AA)),
          ),
          RadioListTile<ColorBlindnessType>(
            title: const Text('Deuteranopia (Azul/Laranja)'),
            subtitle: const Text('Otimizado para daltonismo de verde'),
            value: ColorBlindnessType.deuteranopia,
            groupValue: themeService.colorBlindnessType,
            onChanged: (v) => themeService.setColorBlindnessType(v!),
            secondary: Container(width: 24, height: 24, color: const Color(0xFF003399)),
          ),
          const SizedBox(height: 20),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 16.0),
            child: Text(
              'Nota: As cores de acessibilidade alteram os principais elementos visuais do app para garantir o contraste e a diferenciação necessária.',
              style: TextStyle(fontSize: 12, fontStyle: FontStyle.italic, color: Colors.grey),
            ),
          ),
        ],
      ),
    );
  }
}
