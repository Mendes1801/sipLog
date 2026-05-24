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

  Future<void> sincronizarUsuario() async {
    final response = await post('/usuarios/sync');
    handleResponse(response);
  }

  Future<List<UsuarioResumoDTO>> buscarUsuarios(String query) async {
    final response = await get('/usuarios/buscar', queryParameters: {'q': query});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => UsuarioResumoDTO.fromJson(json)).toList();
  }

  Future<List<UsuarioResumoDTO>> getSeguidores(int idUsuario, {int pagina = 0}) async {
    final response = await get('/usuarios/$idUsuario/seguidores', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => UsuarioResumoDTO.fromJson(json)).toList();
  }

  Future<List<UsuarioResumoDTO>> getSeguindo(int idUsuario, {int pagina = 0}) async {
    final response = await get('/usuarios/$idUsuario/seguindo', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => UsuarioResumoDTO.fromJson(json)).toList();
  }
}
