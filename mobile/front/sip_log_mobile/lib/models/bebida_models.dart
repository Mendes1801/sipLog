class BebidaResumoDTO {
  final int? idBebida;
  final String? nome;
  final String? categoria;

  BebidaResumoDTO({this.idBebida, this.nome, this.categoria});

  factory BebidaResumoDTO.fromJson(Map<String, dynamic> json) {
    return BebidaResumoDTO(
      idBebida: json['idBebida'],
      nome: json['nome'],
      categoria: json['categoria'],
    );
  }
}

class DetalheBebidaDTO {
  final int? idBebida;
  final String? nome;
  final String? fabricante;
  final String? categoria;
  final double? notaMediaGlobal;
  final Map<String, String>? caracteristicas;

  DetalheBebidaDTO({
    this.idBebida,
    this.nome,
    this.fabricante,
    this.categoria,
    this.notaMediaGlobal,
    this.caracteristicas,
  });

  factory DetalheBebidaDTO.fromJson(Map<String, dynamic> json) {
    return DetalheBebidaDTO(
      idBebida: json['idBebida'],
      nome: json['nome'],
      fabricante: json['fabricante'],
      categoria: json['categoria'],
      notaMediaGlobal: (json['notaMediaGlobal'] as num?)?.toDouble(),
      caracteristicas: (json['caracteristicas'] as Map<String, dynamic>?)?.map((k, v) => MapEntry(k, v.toString())),
    );
  }
}

class NovaBebidaDTO {
  final String nome;
  final String fabricante;
  final String categoria;
  final Map<String, String> caracteristicas;

  NovaBebidaDTO({
    required this.nome,
    required this.fabricante,
    required this.categoria,
    required this.caracteristicas,
  });

  Map<String, dynamic> toJson() {
    return {
      'nome': nome,
      'fabricante': fabricante,
      'categoria': categoria,
      'caracteristicas': caracteristicas,
    };
  }
}
