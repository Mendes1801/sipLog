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
  String? _idToken;
  DateTime? _accessTokenExpiration;
  bool _isBusy = false;
  String? _userAvatarUrl;
  int? _userId;

  String? get accessToken => _accessToken;
  bool get isAuthenticated => _accessToken != null;
  bool get isBusy => _isBusy;
  String? get userAvatarUrl => _userAvatarUrl;
  int? get userId => _userId;

  void updateUserData({String? avatarUrl, int? id}) {
    if (avatarUrl != null) _userAvatarUrl = avatarUrl;
    if (id != null) _userId = id;
    notifyListeners();
  }

  void updateAvatarUrl(String? url) {
    _userAvatarUrl = url;
    notifyListeners();
  }

  static const _config = AuthorizationServiceConfiguration(
    authorizationEndpoint: '$_issuer/protocol/openid-connect/auth',
    tokenEndpoint: '$_issuer/protocol/openid-connect/token',
    endSessionEndpoint: '$_issuer/protocol/openid-connect/logout',
  );

  Future<void> login() async {
    _isBusy = true;
    notifyListeners();

    try {
      final AuthorizationTokenResponse? result = await _appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          _clientId,
          _redirectUrl,
          serviceConfiguration: _config,
          scopes: ['openid', 'profile', 'email', 'offline_access'],
          allowInsecureConnections: true,
        ),
      );

      debugPrint('👉 KEYCLOAK RESULT: $result');
      if (result != null) {
        _accessToken = result.accessToken;
        _refreshToken = result.refreshToken;
        _idToken = result.idToken;
        _accessTokenExpiration = result.accessTokenExpirationDateTime;
        
        await _saveTokens(
          _accessToken, 
          _refreshToken, 
          _idToken, 
          _accessTokenExpiration?.millisecondsSinceEpoch
        );
        notifyListeners();
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
    _isBusy = true;
    notifyListeners();

    try {
      // Invalida a sessão no Keycloak
      if (_idToken != null) {
        await _appAuth.endSession(
          EndSessionRequest(
            idTokenHint: _idToken,
            postLogoutRedirectUrl: _redirectUrl,
            serviceConfiguration: _config,
            allowInsecureConnections: true,
          ),
        );
      }
    } catch (e) {
      debugPrint('Erro ao invalidar sessão no Keycloak: $e');
    } finally {
      _accessToken = null;
      _refreshToken = null;
      _idToken = null;
      _accessTokenExpiration = null;
      
      await _secureStorage.deleteAll();
      _isBusy = false;
      notifyListeners();
    }
  }

  Future<void> _saveTokens(String? access, String? refresh, String? id, int? expiry) async {
    if (access != null) await _secureStorage.write(key: 'access_token', value: access);
    if (refresh != null) await _secureStorage.write(key: 'refresh_token', value: refresh);
    if (id != null) await _secureStorage.write(key: 'id_token', value: id);
    if (expiry != null) await _secureStorage.write(key: 'expiry', value: expiry.toString());
  }

  Future<void> tryAutoLogin() async {
    final String? access = await _secureStorage.read(key: 'access_token');
    final String? refresh = await _secureStorage.read(key: 'refresh_token');
    final String? id = await _secureStorage.read(key: 'id_token');
    final String? expiryStr = await _secureStorage.read(key: 'expiry');

    if (access != null) {
      _accessToken = access;
      _refreshToken = refresh;
      _idToken = id;
      if (expiryStr != null) {
        _accessTokenExpiration = DateTime.fromMillisecondsSinceEpoch(int.parse(expiryStr));
      }
      notifyListeners();
    }
  }

  Future<String?> getValidAccessToken() async {
    if (_accessToken == null) return null;

    // Se o token expira em menos de 1 minuto, renova
    final now = DateTime.now();
    if (_accessTokenExpiration == null || _accessTokenExpiration!.isBefore(now.add(const Duration(minutes: 1)))) {
      await _refreshAccessToken();
    }

    return _accessToken;
  }

  Future<void> _refreshAccessToken() async {
    if (_refreshToken == null) return;

    try {
      debugPrint('🔄 Renovando Access Token...');
      final result = await _appAuth.token(
        TokenRequest(
          _clientId,
          _redirectUrl,
          refreshToken: _refreshToken,
          serviceConfiguration: _config,
          allowInsecureConnections: true,
        ),
      );

      if (result != null) {
        _accessToken = result.accessToken;
        _refreshToken = result.refreshToken;
        _idToken = result.idToken;
        _accessTokenExpiration = result.accessTokenExpirationDateTime;

        await _saveTokens(
          _accessToken, 
          _refreshToken, 
          _idToken, 
          _accessTokenExpiration?.millisecondsSinceEpoch
        );
        debugPrint('✅ Token renovado com sucesso!');
      }
    } catch (e) {
      debugPrint('❌ Erro ao renovar token: $e');
      // Se falhar o refresh, desloga o usuário
      await logout();
    }
  }
}
