import '../models/comentario_model.dart';

class MockComentarioService {
  // Simulando um banco de dados temporário na memória
  final List<ComentarioModel> _comentariosIniciais = [
    ComentarioModel(
      id: 1, texto: 'Nossa, que escolha fantástica! Preciso experimentar.', tempoDecorrido: 'Há 1h', autorId: 201, autorNome: 'Celso', autorFoto: 'https://i.pravatar.cc/150?img=11'
    ),
    ComentarioModel(
      id: 2, texto: 'Harmonizou muito bem?', tempoDecorrido: 'Há 30m', autorId: 202, autorNome: 'Carla', autorFoto: 'https://i.pravatar.cc/150?img=5'
    ),
  ];

  // Simula o GET /api/v1/experiencias/{id}/comentarios
  Future<List<ComentarioModel>> getComentarios(int idPost, {int pagina = 0}) async {
    await Future.delayed(const Duration(seconds: 1)); // Delay da internet
    return List.from(_comentariosIniciais); 
  }

  // Simula o POST /api/v1/experiencias/{id}/comentarios
  Future<ComentarioModel> adicionarComentario(int idPost, String texto) async {
    await Future.delayed(const Duration(milliseconds: 500));
    final novoComentario = ComentarioModel(
      id: DateTime.now().millisecondsSinceEpoch, // ID falso
      texto: texto,
      tempoDecorrido: 'Agora mesmo',
      autorId: 999,
      autorNome: 'Bruno (Você)',
      autorFoto: null, // Puxará o icone default da Camila
    );
    _comentariosIniciais.insert(0, novoComentario); // Adiciona no topo
    return novoComentario;
  }
}