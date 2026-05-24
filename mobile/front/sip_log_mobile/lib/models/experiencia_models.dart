import 'user_models.dart';

class NovaExperienciaDTO {
  final int idBebida;
  final double nota;
  final String? comentario;
  final String visibilidade;
  final String fotoPostUrl;
  final String? localizacao;

  NovaExperienciaDTO({
    required this.idBebida,
    required this.nota,
    this.comentario,
    required this.visibilidade,
    required this.fotoPostUrl,
    this.localizacao,
  });

  Map<String, dynamic> toJson() {
    return {
      'idBebida': idBebida,
      'nota': nota,
      'comentario': comentario,
      'visibilidade': visibilidade,
      'fotoPostUrl': fotoPostUrl,
      'localizacao': localizacao,
    };
  }
}

class ComentarioResponseDTO {
  final int? id;
  final String? texto;
  final String? tempoDecorrido;
  final UsuarioResumoDTO? autor;

  ComentarioResponseDTO({this.id, this.texto, this.tempoDecorrido, this.autor});

  factory ComentarioResponseDTO.fromJson(Map<String, dynamic> json) {
    return ComentarioResponseDTO(
      id: json['id'],
      texto: json['texto'],
      tempoDecorrido: json['tempoDecorrido'],
      autor: json['autor'] != null ? UsuarioResumoDTO.fromJson(json['autor']) : null,
    );
  }
}

class NovoComentarioDTO {
  final String texto;

  NovoComentarioDTO({required this.texto});

  Map<String, dynamic> toJson() {
    return {'texto': texto};
  }
}

class PaginaBffComentarioDTO {
  final List<ComentarioResponseDTO> content;
  final int number;
  final int totalElements;
  final int totalPages;
  final bool last;

  PaginaBffComentarioDTO({
    required this.content,
    required this.number,
    required this.totalElements,
    required this.totalPages,
    required this.last,
  });

  factory PaginaBffComentarioDTO.fromJson(Map<String, dynamic> json) {
    return PaginaBffComentarioDTO(
      content: (json['content'] as List).map((i) => ComentarioResponseDTO.fromJson(i)).toList(),
      number: json['number'],
      totalElements: json['totalElements'],
      totalPages: json['totalPages'],
      last: json['last'],
    );
  }
}
