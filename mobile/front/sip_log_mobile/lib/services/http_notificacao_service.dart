import '../models/notificacao_models.dart';
import 'http_api_service.dart';

class HttpNotificacaoService extends HttpApiService {
  HttpNotificacaoService(super.authService);

  Future<PaginaBffNotificacaoDTO> listarNotificacoes({int pagina = 0}) async {
    final response = await get('/notificacoes', queryParameters: {'pagina': pagina.toString()});
    return PaginaBffNotificacaoDTO.fromJson(handleResponse(response));
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
