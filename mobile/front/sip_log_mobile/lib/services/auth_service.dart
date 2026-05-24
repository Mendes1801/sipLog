import 'package:flutter/foundation.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthService extends ChangeNotifier {
  final FlutterAppAuth _appAuth = const FlutterAppAuth();
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  static const String _clientId = 'sipLog';
  static const String _redirectUrl = 'com.example.siplogmobile://oauth2redirect';
  static const String _issuer = 'http://ec2-52-2-112-100.compute-1.amazonaws.com:8080/realms/BFF';
  static const String _discoveryUrl = '$_issuer/.well-known/openid-configuration';

  String? _accessToken;
  String? _refreshToken;
  bool _isBusy = false;

  String? get accessToken => _accessToken;
  bool get isAuthenticated => _accessToken != null;
  bool get isBusy => _isBusy;

  Future<void> login() async {
    _isBusy = true;
    notifyListeners();

    try {
      final AuthorizationTokenResponse? result = await _appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          _clientId,
          _redirectUrl,
          serviceConfiguration: AuthorizationServiceConfiguration(
            authorizationEndpoint: '$_issuer/protocol/openid-connect/auth',
            tokenEndpoint: '$_issuer/protocol/openid-connect/token',
            endSessionEndpoint: '$_issuer/protocol/openid-connect/logout',
          ),
          scopes: ['openid', 'profile', 'email', 'offline_access'],

          allowInsecureConnections: true,
          // Mantido sem parâmetros adicionais que gerem conflitos no builder nativo
        ),
      );

      debugPrint('👉 KEYCLOAK RESULT: $result');
      if (result != null) {
        await _saveTokens(result.accessToken, result.refreshToken);
        _accessToken = result.accessToken;
        _refreshToken = result.refreshToken;
      }
    } catch (e, s) {
      print('❌ ERRO CRÍTICO NO LOGIN DART: $e');
      debugPrint('Error during login: $e\n$s');
    } finally {
      _isBusy = false;
      notifyListeners();
    }
  }

  Future<void> logout() async {
    _accessToken = null;
    _refreshToken = null;
    await _secureStorage.delete(key: 'access_token');
    await _secureStorage.delete(key: 'refresh_token');
    notifyListeners();
  }

  Future<void> _saveTokens(String? accessToken, String? refreshToken) async {
    if (accessToken != null) {
      await _secureStorage.write(key: 'access_token', value: accessToken);
    }
    if (refreshToken != null) {
      await _secureStorage.write(key: 'refresh_token', value: refreshToken);
    }
  }

  Future<void> tryAutoLogin() async {
    final String? savedAccessToken = await _secureStorage.read(key: 'access_token');
    final String? savedRefreshToken = await _secureStorage.read(key: 'refresh_token');

    if (savedAccessToken != null) {
      _accessToken = savedAccessToken;
      _refreshToken = savedRefreshToken;
      notifyListeners();
      // Em um cenário real, deveríamos validar o token aqui ou tentar um refresh
    }
  }

  Future<String?> getValidAccessToken() async {
    // Implementar lógica de refresh aqui se necessário
    return _accessToken;
  }
}
