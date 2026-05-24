import '../models/bebida_models.dart';
import 'http_api_service.dart';

class HttpBebidaService extends HttpApiService {
  HttpBebidaService(super.authService);

  Future<List<BebidaResumoDTO>> buscarBebidas(String query) async {
    final response = await get('/bebidas/buscar', queryParameters: {'q': query});
    final List<dynamic> data = handleResponse(response);
    return data.map((json) => BebidaResumoDTO.fromJson(json)).toList();
  }

  Future<DetalheBebidaDTO> buscarBebidaPorId(int id) async {
    final response = await get('/bebidas/$id', queryParameters: {'id': id.toString()});
    return DetalheBebidaDTO.fromJson(handleResponse(response));
  }

  Future<BebidaResumoDTO> adicionarBebida(NovaBebidaDTO novaBebida) async {
    final response = await post('/bebidas', body: novaBebida.toJson());
    return BebidaResumoDTO.fromJson(handleResponse(response));
  }
}
