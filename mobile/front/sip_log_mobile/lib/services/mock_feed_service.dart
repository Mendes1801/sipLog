import '../models/feed_response_model.dart';

class MockFeedService {
  // O "Future" indica que isso é uma chamada assíncrona, igual a uma requisição HTTP real.
  Future<List<FeedResponseModel>> getFeedGlobal() async {
    // Simulando o tempo de ida e volta do seu backend (BFF)
    await Future.delayed(const Duration(milliseconds: 1500));

    return [
      FeedResponseModel(
        idPost: 1,
        idUsuario: 101,
        nomeAutor: 'Clarissa',
        fotoAvatarUrl: 'https://i.pravatar.cc/150?img=5', // URL falsa para gerar um avatar
        tempoDecorrido: 'Há 2h',
        local: 'Em casa',
        fotoPostUrl: 'https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?auto=format&fit=crop&w=500&q=60', // Foto de uma taça de vinho
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
        fotoPostUrl: 'https://images.unsplash.com/photo-1584225065152-4a145afaa3cc?auto=format&fit=crop&w=500&q=60', // Foto de bebida artesanal
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
  }
}