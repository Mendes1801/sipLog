import '../models/feed_response_model.dart';

class MockFeedService {
  // Lista estática que funciona como nosso "banco de dados" temporário
  static final List<FeedResponseModel> _feedData = [
    FeedResponseModel(
      idPost: 1,
      idUsuario: 101,
      nomeAutor: 'Clarissa',
      fotoAvatarUrl: 'https://i.pravatar.cc/150?img=5',
      tempoDecorrido: 'Há 2h',
      local: 'Em casa',
      fotoPostUrl: 'https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?auto=format&fit=crop&w=500&q=60',
      idBebida: 201,
      nomeBebida: 'Cabernet Sauvignon Reserva',
      categoriaBebida: 'Vinho Tinto',
      nota: 5.0,
      comentario: 'Acompanhando um risoto incrível. A harmonização ficou perfeita, encorpado na medida certa!',
      curtidoPorMim: true,
      totalCurtidas: 42,
      totalComentarios: 3,
    ),
    FeedResponseModel(
      idPost: 2,
      idUsuario: 102,
      nomeAutor: 'Camila',
      fotoAvatarUrl: 'https://i.pravatar.cc/150?img=9',
      tempoDecorrido: 'Há 5h',
      local: 'Cantina Italiana',
      fotoPostUrl: 'https://images.unsplash.com/photo-1584225065152-4a145afaa3cc?auto=format&fit=crop&w=500&q=60',
      idBebida: 202,
      nomeBebida: 'IPA Artesanal',
      categoriaBebida: 'Cerveja',
      nota: 4.0,
      comentario: 'Amargor no ponto, muito refrescante. Ótima escolha para o final de semana.',
      curtidoPorMim: false,
      totalCurtidas: 15,
      totalComentarios: 1,
    ),
  ];

  Future<List<FeedResponseModel>> getFeedGlobal() async {
    // Simula o tempo de rede apenas na primeira vez
    await Future.delayed(const Duration(milliseconds: 800));
    return _feedData;
  }

  // Método para o "remendo" de adicionar post no cache da memória
  static void adicionarNovoPost(FeedResponseModel novoPost) {
    _feedData.insert(0, novoPost); // Insere no topo do feed
  }
}