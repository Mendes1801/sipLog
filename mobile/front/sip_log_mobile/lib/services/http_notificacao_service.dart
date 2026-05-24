import '../models/notificacao_models.dart';
import 'http_api_service.dart';

class HttpNotificacaoService extends HttpApiService {
  HttpNotificacaoService(super.authService);

  Future<List<NotificacaoResponseDTO>> listarNotificacoes({int pagina = 0}) async {
    final response = await get('/notificacoes', queryParameters: {'pagina': pagina.toString()});
    final Map<String, dynamic> data = handleResponse(response);
    final List<dynamic> contentList = data['content'];
    return contentList.map((json) => NotificacaoResponseDTO.fromJson(json)).toList();
  }

  Future<ContagemNotificacoesDTO> contarNaoLidas() async {
    final response = await get('/notificacoes/nao-lidas/count');
    return ContagemNotificacoesDTO.fromJson(handleResponse(response));
  }

  Future<void> marcarComoLida(int id) async {
    final response = await patch('/notificacoes/$id/lida');
    handleResponse(response);
  }
}
