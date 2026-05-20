class ComentarioModel {
  final int id;
  final String texto;
  final String tempoDecorrido;
  
  // Dados do Autor (UsuarioResumoDTO no backend)
  final int autorId;
  final String autorNome;
  final String? autorFoto;

  ComentarioModel({
    required this.id,
    required this.texto,
    required this.tempoDecorrido,
    required this.autorId,
    required this.autorNome,
    this.autorFoto,
  });

  factory ComentarioModel.fromJson(Map<String, dynamic> json) {
    return ComentarioModel(
      id: json['id'],
      texto: json['texto'],
      tempoDecorrido: json['tempoDecorrido'],
      autorId: json['autor']['id'],
      autorNome: json['autor']['nome'],
      autorFoto: json['autor']['fotoAvatarUrl'],
    );
  }
}