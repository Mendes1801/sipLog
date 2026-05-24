import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/notificacao_models.dart';
import '../services/http_notificacao_service.dart';

class NotificacaoScreen extends StatefulWidget {
  const NotificacaoScreen({super.key});

  @override
  State<NotificacaoScreen> createState() => _NotificacaoScreenState();
}

class _NotificacaoScreenState extends State<NotificacaoScreen> {
  List<NotificacaoResponseDTO> _notificacoes = [];
  bool _carregando = true;
  int _paginaAtual = 0;
  bool _temMais = true;
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _carregarNotificacoes();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >= _scrollController.position.maxScrollExtent - 200 &&
        !_carregando &&
        _temMais) {
      _carregarNotificacoes();
    }
  }

  Future<void> _carregarNotificacoes({bool reset = false}) async {
    if (reset) {
      _paginaAtual = 0;
      _temMais = true;
      _notificacoes = [];
    }

    if (!_temMais) return;

    setState(() => _carregando = true);
    final notificacaoService = Provider.of<HttpNotificacaoService>(context, listen: false);

    try {
      final novas = await notificacaoService.listarNotificacoes(pagina: _paginaAtual);
      if (mounted) {
        setState(() {
          _notificacoes.addAll(novas);
          _carregando = false;
          _paginaAtual++;
          if (novas.length < 20) {
            _temMais = false;
          }
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar notificações: $e')),
        );
      }
    }
  }

  Future<void> _marcarComoLida(NotificacaoResponseDTO notificacao) async {
    if (notificacao.lida == true) return;

    final notificacaoService = Provider.of<HttpNotificacaoService>(context, listen: false);
    try {
      await notificacaoService.marcarComoLida(notificacao.idNotificacao!);
      setState(() {
        // Encontrar o índice da notificação e atualizar o estado local
        final index = _notificacoes.indexWhere((n) => n.idNotificacao == notificacao.idNotificacao);
        if (index != -1) {
          _notificacoes[index] = NotificacaoResponseDTO(
            idNotificacao: notificacao.idNotificacao,
            tipo: notificacao.tipo,
            usuarioOrigem: notificacao.usuarioOrigem,
            mensagem: notificacao.mensagem,
            tempoAtras: notificacao.tempoAtras,
            lida: true,
          );
        }
      });
    } catch (e) {
      debugPrint('Erro ao marcar como lida: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: () => _carregarNotificacoes(reset: true),
      color: Colors.deepPurple,
      child: _notificacoes.isEmpty && !_carregando
          ? const Center(child: Text('Nenhuma notificação encontrada.'))
          : ListView.separated(
              controller: _scrollController,
              itemCount: _notificacoes.length + (_temMais ? 1 : 0),
              separatorBuilder: (context, index) => const Divider(height: 1),
              itemBuilder: (context, index) {
                if (index == _notificacoes.length) {
                  return const Padding(
                    padding: EdgeInsets.all(15.0),
                    child: Center(child: CircularProgressIndicator()),
                  );
                }

                final notificacao = _notificacoes[index];
                return ListTile(
                  leading: CircleAvatar(
                    backgroundImage: notificacao.usuarioOrigem?.fotoAvatarUrl != null
                        ? NetworkImage(notificacao.usuarioOrigem!.fotoAvatarUrl!)
                        : const AssetImage('assets/images/BT_Profile.png') as ImageProvider,
                  ),
                  title: Text(
                    notificacao.mensagem ?? 'Nova notificação',
                    style: TextStyle(
                      fontWeight: notificacao.lida == false ? FontWeight.bold : FontWeight.normal,
                    ),
                  ),
                  subtitle: Text(notificacao.tempoAtras ?? ''),
                  trailing: notificacao.lida == false
                      ? const Icon(Icons.circle, color: Colors.deepPurple, size: 12)
                      : null,
                  onTap: () => _marcarComoLida(notificacao),
                  tileColor: notificacao.lida == false ? Colors.deepPurple.withOpacity(0.05) : null,
                );
              },
            ),
    );
  }
}
