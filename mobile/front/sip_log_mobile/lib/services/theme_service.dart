import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

enum ColorBlindnessType { none, protanopia, deuteranopia }

class ThemeService extends ChangeNotifier {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  
  ThemeMode _themeMode = ThemeMode.light;
  ColorBlindnessType _colorBlindnessType = ColorBlindnessType.none;

  ThemeMode get themeMode => _themeMode;
  ColorBlindnessType get colorBlindnessType => _colorBlindnessType;

  ThemeService() {
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final mode = await _storage.read(key: 'theme_mode');
    final cbType = await _storage.read(key: 'cb_type');

    if (mode == 'dark') _themeMode = ThemeMode.dark;
    if (mode == 'light') _themeMode = ThemeMode.light;

    if (cbType == 'protanopia') _colorBlindnessType = ColorBlindnessType.protanopia;
    if (cbType == 'deuteranopia') _colorBlindnessType = ColorBlindnessType.deuteranopia;

    notifyListeners();
  }

  Future<void> toggleThemeMode(bool isDark) async {
    _themeMode = isDark ? ThemeMode.dark : ThemeMode.light;
    await _storage.write(key: 'theme_mode', value: isDark ? 'dark' : 'light');
    notifyListeners();
  }

  Future<void> setColorBlindnessType(ColorBlindnessType type) async {
    _colorBlindnessType = type;
    await _storage.write(key: 'cb_type', value: type.name);
    notifyListeners();
  }

  ThemeData getThemeData(bool isDark) {
    // Cor base para a paleta
    Color seedColor = const Color(0xFF673AB7); // Deep Purple
    
    if (_colorBlindnessType == ColorBlindnessType.protanopia) {
      seedColor = const Color(0xFF0055AA); // Azul Cobalto
    } else if (_colorBlindnessType == ColorBlindnessType.deuteranopia) {
      seedColor = const Color(0xFF003399); // Azul Escuro
    }

    // Criamos o ColorScheme. fromSeed gera tons claros e escuros automaticamente
    final colorScheme = ColorScheme.fromSeed(
      seedColor: seedColor,
      brightness: isDark ? Brightness.dark : Brightness.light,
      primary: isDark ? null : seedColor, // No dark, deixa o seedColor gerar um tom mais legível
    );

    final base = isDark ? ThemeData.dark() : ThemeData.light();

    return base.copyWith(
      primaryColor: colorScheme.primary,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: isDark ? const Color(0xFF121212) : Colors.grey[50],
      appBarTheme: AppBarTheme(
        backgroundColor: isDark ? const Color(0xFF1E1E1E) : colorScheme.primary,
        foregroundColor: Colors.white,
        elevation: 0,
        centerTitle: true,
      ),
      tabBarTheme: base.tabBarTheme.copyWith(
        labelColor: Colors.white,
        unselectedLabelColor: Colors.white.withOpacity(0.7),
        indicatorColor: Colors.white,
        indicatorSize: TabBarIndicatorSize.tab,
      ),
      bottomNavigationBarTheme: BottomNavigationBarThemeData(
        backgroundColor: isDark ? const Color(0xFF1E1E1E) : Colors.white,
        selectedItemColor: isDark ? Colors.white : colorScheme.primary,
        unselectedItemColor: Colors.grey,
        type: BottomNavigationBarType.fixed,
        elevation: 8,
      ),
      cardTheme: base.cardTheme.copyWith(
        color: isDark ? const Color(0xFF1E1E1E) : Colors.white,
        elevation: 2,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: colorScheme.primary,
          foregroundColor: colorScheme.onPrimary,
          elevation: 0,
          padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 20),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          textStyle: const TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          side: BorderSide(color: isDark ? Colors.white : colorScheme.primary),
          foregroundColor: isDark ? Colors.white : colorScheme.primary,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        ),
      ),
      iconTheme: base.iconTheme.copyWith(
        color: isDark ? Colors.white : colorScheme.primary,
      ),
      dividerTheme: base.dividerTheme.copyWith(
        color: isDark ? Colors.white12 : Colors.grey[300],
      ),
      sliderTheme: base.sliderTheme.copyWith(
        activeTrackColor: colorScheme.primary,
        thumbColor: colorScheme.primary,
      ),
      inputDecorationTheme: InputDecorationTheme(
        border: const OutlineInputBorder(),
        focusedBorder: OutlineInputBorder(
          borderSide: BorderSide(color: colorScheme.primary, width: 2),
        ),
        labelStyle: TextStyle(color: isDark ? Colors.white70 : colorScheme.primary),
      ),
      textTheme: base.textTheme.apply(
        bodyColor: isDark ? Colors.white : Colors.black87,
        displayColor: isDark ? Colors.white : Colors.black,
      ),
    );
  }
}
