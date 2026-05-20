import 'package:flutter/material.dart';
import '../models/feed_response_model.dart';
import '../services/mock_feed_service.dart';
import '../widgets/post_card.dart';

class FeedScreen extends StatefulWidget {
  const FeedScreen({super.key});

  @override
  State<FeedScreen> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {
  final MockFeedService _feedService = MockFeedService();
  late Future<List<FeedResponseModel>> _feedFuture;

  @override
  void initState() {
    super.initState();
    // Inicia a requisição (mesmo sendo mock) assim que a tela abre
    _feedFuture = _feedService.getFeedGlobal();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<FeedResponseModel>>(
      future: _feedFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator()); // Efeito visual de carregamento
        } else if (snapshot.hasError) {
          return Center(child: Text('Erro ao carregar o feed: ${snapshot.error}'));
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return const Center(child: Text('Nenhuma degustação encontrada no momento.'));
        }

        // Se chegou aqui, temos dados!
        final posts = snapshot.data!;
        
        return ListView.builder(
          itemCount: posts.length,
          itemBuilder: (context, index) {
            return PostCard(post: posts[index]);
          },
        );
      },
    );
  }
}