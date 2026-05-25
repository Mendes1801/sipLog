import 'user_models.dart';

class UsuarioOrigemDTO {
  final int? idUsuario;
  final String? nome;
  final String? fotoAvatarUrl;

  UsuarioOrigemDTO({this.idUsuario, this.nome, this.fotoAvatarUrl});

  factory UsuarioOrigemDTO.fromJson(Map<String, dynamic> json) {
    return UsuarioOrigemDTO(
      idUsuario: json['idUsuario'] ?? json['id'], // Tenta as duas possibilidades comuns
      nome: json['nome'],
      fotoAvatarUrl: json['fotoAvatarUrl'],
    );
  }
}

class NotificacaoResponseDTO {
  final int? idNotificacao;
  final String? tipo;
  final UsuarioOrigemDTO? usuarioOrigem;
  final String? mensagem;
  final String? tempoAtras;
  final bool? lida;

  NotificacaoResponseDTO({
    this.idNotificacao,
    this.tipo,
    this.usuarioOrigem,
    this.mensagem,
    this.tempoAtras,
    this.lida,
  });

  factory NotificacaoResponseDTO.fromJson(Map<String, dynamic> json) {
    return NotificacaoResponseDTO(
      idNotificacao: json['idNotificacao'],
      tipo: json['tipo'],
      usuarioOrigem: json['usuarioOrigem'] != null ? UsuarioOrigemDTO.fromJson(json['usuarioOrigem']) : null,
      mensagem: json['mensagem'],
      tempoAtras: json['tempoAtras'],
      lida: json['lida'],
    );
  }
}

class ContagemNotificacoesDTO {
  final int? total;

  ContagemNotificacoesDTO({this.total});

  factory ContagemNotificacoesDTO.fromJson(Map<String, dynamic> json) {
    return ContagemNotificacoesDTO(
      total: json['total'],
    );
  }
}

class PaginaBffNotificacaoDTO {
  final List<NotificacaoResponseDTO> content;
  final int number;
  final int totalElements;
  final int totalPages;
  final bool last;

  PaginaBffNotificacaoDTO({
    required this.content,
    required this.number,
    required this.totalElements,
    required this.totalPages,
    required this.last,
  });

  factory PaginaBffNotificacaoDTO.fromJson(Map<String, dynamic> json) {
    return PaginaBffNotificacaoDTO(
      content: (json['content'] as List).map((i) => NotificacaoResponseDTO.fromJson(i)).toList(),
      number: json['number'],
      totalElements: json['totalElements'],
      totalPages: json['totalPages'],
      last: json['last'],
    );
  }
}
