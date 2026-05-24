import 'dart:convert';
import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'auth_service.dart';

class HttpApiService {
  final String baseUrl = 'http://ec2-52-2-112-100.compute-1.amazonaws.com:8081/api/v1';
  final AuthService authService;

  HttpApiService(this.authService);

  Future<Map<String, String>> _getHeaders() async {
    final token = await authService.getValidAccessToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  Future<http.Response> get(String path, {Map<String, String>? queryParameters}) async {
    var uri = Uri.parse('$baseUrl$path');
    if (queryParameters != null) {
      uri = uri.replace(queryParameters: queryParameters);
    }
    final headers = await _getHeaders();
    return await http.get(uri, headers: headers);
  }

  Future<http.Response> post(String path, {dynamic body}) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = await _getHeaders();
    return await http.post(uri, headers: headers, body: json.encode(body));
  }

  Future<http.Response> put(String path, {dynamic body}) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = await _getHeaders();
    return await http.put(uri, headers: headers, body: json.encode(body));
  }

  Future<http.Response> patch(String path, {dynamic body}) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = await _getHeaders();
    return await http.patch(uri, headers: headers, body: json.encode(body));
  }

  Future<http.Response> delete(String path) async {
    final uri = Uri.parse('$baseUrl$path');
    final headers = await _getHeaders();
    return await http.delete(uri, headers: headers);
  }

  Future<String> upload(String filePath) async {
    final uri = Uri.parse('$baseUrl/upload');
    final token = await authService.getValidAccessToken();
    
    var request = http.MultipartRequest('POST', uri);
    request.headers.addAll({
      if (token != null) 'Authorization': 'Bearer $token',
    });
    
    request.files.add(await http.MultipartFile.fromPath('file', filePath));
    
    final streamedResponse = await request.send();
    final response = await http.Response.fromStream(streamedResponse);
    
    final dynamic data = handleResponse(response);
    return data['url'];
  }

  dynamic handleResponse(http.Response response) {
    debugPrint('Resposta da API: [${response.statusCode}] em ${response.request?.url}');
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) return null;
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      debugPrint('ERRO NA API: ${response.statusCode} - Conteúdo: ${response.body}');
      throw Exception('Erro na API: ${response.statusCode} - ${response.body}');
    }
  }
}
