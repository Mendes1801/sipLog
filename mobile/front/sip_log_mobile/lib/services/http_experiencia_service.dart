import '../models/experiencia_models.dart';
import '../models/feed_response_model.dart';
import 'http_api_service.dart';

class HttpExperienciaService extends HttpApiService {
  HttpExperienciaService(super.authService);

  Future<void> registrarNovaExperiencia(NovaExperienciaDTO nova) async {
    final response = await post('/experiencias', body: nova.toJson());
    handleResponse(response);
  }

  Future<void> alternarCurtida(int id) async {
    final response = await post('/experiencias/$id/curtir');
    handleResponse(response);
  }

  Future<PaginaBffComentarioDTO> listarComentarios(int id, {int pagina = 0}) async {
    final response = await get('/experiencias/$id/comentarios', queryParameters: {'pagina': pagina.toString()});
    return PaginaBffComentarioDTO.fromJson(handleResponse(response));
  }

  Future<ComentarioResponseDTO> adicionarComentario(int id, NovoComentarioDTO novo) async {
    final response = await post('/experiencias/$id/comentarios', body: novo.toJson());
    return ComentarioResponseDTO.fromJson(handleResponse(response));
  }

  Future<void> deletarExperiencia(int id) async {
    final response = await delete('/experiencias/$id');
    handleResponse(response);
  }

  Future<FeedResponseModel> buscarExperienciaPorId(int id) async {
    final response = await get('/experiencias/$id');
    return FeedResponseModel.fromJson(handleResponse(response));
  }
}
