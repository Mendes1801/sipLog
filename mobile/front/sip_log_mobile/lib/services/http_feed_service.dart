import '../models/feed_response_model.dart';
import 'http_api_service.dart';

class HttpFeedService extends HttpApiService {
  HttpFeedService(super.authService);

  Future<List<FeedResponseModel>> getFeedGlobal({int pagina = 0}) async {
    final response = await get('/feed/global', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => FeedResponseModel.fromJson(json)).toList();
  }

  Future<List<FeedResponseModel>> getFeedMe({int pagina = 0}) async {
    final response = await get('/feed/me', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => FeedResponseModel.fromJson(json)).toList();
  }

  Future<List<FeedResponseModel>> getFeedAmigos({int pagina = 0}) async {
    final response = await get('/feed/amigos', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => FeedResponseModel.fromJson(json)).toList();
  }

  Future<List<FeedResponseModel>> getFeedDeUsuario(int idUsuario, {int pagina = 0}) async {
    final response = await get('/feed/usuarios/$idUsuario', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => FeedResponseModel.fromJson(json)).toList();
  }
}
