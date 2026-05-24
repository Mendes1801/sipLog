import '../models/user_models.dart';
import 'http_api_service.dart';

class HttpUserService extends HttpApiService {
  HttpUserService(super.authService);

  Future<PerfilDTO> getMeuPerfil() async {
    final response = await get('/usuarios/me');
    return PerfilDTO.fromJson(handleResponse(response));
  }

  Future<void> atualizarMeuPerfil(UsuarioUpdateDTO update) async {
    final response = await put('/usuarios/me', body: update.toJson());
    handleResponse(response);
  }

  Future<PerfilDTO> getPerfilUsuario(int idUsuario) async {
    final response = await get('/usuarios/$idUsuario');
    return PerfilDTO.fromJson(handleResponse(response));
  }

  Future<void> alternarSeguir(int idAlvo) async {
    final response = await post('/usuarios/$idAlvo/seguir');
    handleResponse(response);
  }
}
