class FeedResponseModel {
  final int idPost;

  // Usuario
  final int idUsuario;
  final String nomeAutor;
  final String? fotoAvatarUrl; // O "?" indica que pode vir nulo da API

  // Experiencia
  final String tempoDecorrido;
  final String? local;
  final String? fotoPostUrl;

  // Bebida
  final int idBebida;
  final String nomeBebida;
  final String categoriaBebida;
  final double nota;
  final String? comentario;

  // Engajamento
  final bool curtidoPorMim;
  final int totalCurtidas;
  final int totalComentarios;

  FeedResponseModel({
    required this.idPost,
    required this.idUsuario,
    required this.nomeAutor,
    this.fotoAvatarUrl,
    required this.tempoDecorrido,
    this.local,
    this.fotoPostUrl,
    required this.idBebida,
    required this.nomeBebida,
    required this.categoriaBebida,
    required this.nota,
    this.comentario,
    required this.curtidoPorMim,
    required this.totalCurtidas,
    required this.totalComentarios,
  });

  // Função mágica que converte o JSON do seu backend para a classe Dart
  factory FeedResponseModel.fromJson(Map<String, dynamic> json) {
    return FeedResponseModel(
      idPost: json['idPost'],
      idUsuario: json['idUsuario'],
      nomeAutor: json['nomeAutor'],
      fotoAvatarUrl: json['fotoAvatarUrl'],
      tempoDecorrido: json['tempoDecorrido'],
      local: json['local'],
      fotoPostUrl: json['fotoPostUrl'],
      idBebida: json['idBebida'],
      nomeBebida: json['nomeBebida'],
      categoriaBebida: json['categoriaBebida'],
      nota: (json['nota'] as num).toDouble(), // Evita erros se o backend mandar inteiro
      comentario: json['comentario'],
      curtidoPorMim: json['curtidoPorMim'] ?? false,
      totalCurtidas: json['totalCurtidas'] ?? 0,
      totalComentarios: json['totalComentarios'] ?? 0,
    );
  }
}