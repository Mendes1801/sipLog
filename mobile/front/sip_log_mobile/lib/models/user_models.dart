class UsuarioResumoDTO {
  final int? id;
  final String? nome;
  final String? fotoAvatarUrl;

  UsuarioResumoDTO({this.id, this.nome, this.fotoAvatarUrl});

  factory UsuarioResumoDTO.fromJson(Map<String, dynamic> json) {
    return UsuarioResumoDTO(
      id: json['id'],
      nome: json['nome'],
      fotoAvatarUrl: json['fotoAvatarUrl'],
    );
  }
}

class UsuarioPerfilDTO {
  final int? idUsuario;
  final String? nome;
  final String? bio;
  final String? fotoAvatarUrl;

  UsuarioPerfilDTO({this.idUsuario, this.nome, this.bio, this.fotoAvatarUrl});

  factory UsuarioPerfilDTO.fromJson(Map<String, dynamic> json) {
    return UsuarioPerfilDTO(
      idUsuario: json['idUsuario'],
      nome: json['nome'],
      bio: json['bio'],
      fotoAvatarUrl: json['fotoAvatarUrl'],
    );
  }
}

class EstatisticasDTO {
  final int? totalDegustacoes;
  final double? notaMediaGlobal;
  final int? seguindo;
  final int? seguidores;

  EstatisticasDTO({this.totalDegustacoes, this.notaMediaGlobal, this.seguindo, this.seguidores});

  factory EstatisticasDTO.fromJson(Map<String, dynamic> json) {
    return EstatisticasDTO(
      totalDegustacoes: json['totalDegustacoes'],
      notaMediaGlobal: (json['notaMediaGlobal'] as num?)?.toDouble(),
      seguindo: json['seguindo'],
      seguidores: json['seguidores'],
    );
  }
}

class PerfilDTO {
  final UsuarioPerfilDTO? usuario;
  final EstatisticasDTO? estatisticas;
  final bool? seguindoPorMim;

  PerfilDTO({this.usuario, this.estatisticas, this.seguindoPorMim});

  factory PerfilDTO.fromJson(Map<String, dynamic> json) {
    return PerfilDTO(
      usuario: json['usuario'] != null ? UsuarioPerfilDTO.fromJson(json['usuario']) : null,
      estatisticas: json['estatisticas'] != null ? EstatisticasDTO.fromJson(json['estatisticas']) : null,
      seguindoPorMim: json['seguindoPorMim'],
    );
  }
}

class UsuarioUpdateDTO {
  final String? nome;
  final String? bio;
  final String? fotoAvatarUrl;

  UsuarioUpdateDTO({this.nome, this.bio, this.fotoAvatarUrl});

  Map<String, dynamic> toJson() {
    return {
      if (nome != null) 'nome': nome,
      if (bio != null) 'bio': bio,
      if (fotoAvatarUrl != null) 'fotoAvatarUrl': fotoAvatarUrl,
    };
  }
}
