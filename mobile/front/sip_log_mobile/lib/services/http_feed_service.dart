import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/feed_response_model.dart';

class HttpFeedService {
  // Substitua pela URL do seu BFF quando for rodar no celular/emulador
  final String baseUrl = 'http://localhost:8081/api/v1';

  Future<List<FeedResponseModel>> getFeedGlobal({int pagina = 0}) async {
    final url = Uri.parse('$baseUrl/feed/global?pagina=$pagina');

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        // O Flutter decodifica o JSON inteiro
        final Map<String, dynamic> jsonDecoded = json.decode(utf8.decode(response.bodyBytes));
        
        // Pega apenas a chave "content" do seu PaginaBffDTOReciveFeedResponseDTO
        final List<dynamic> contentList = jsonDecoded['content'];
        
        // Transforma a lista de mapas JSON em objetos Dart
        return contentList.map((json) => FeedResponseModel.fromJson(json)).toList();
      } else {
        throw Exception('Erro no backend: Código ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Falha ao conectar no servidor: $e');
    }
  }
}