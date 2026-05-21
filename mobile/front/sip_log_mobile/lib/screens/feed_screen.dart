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
  List<FeedResponseModel> _posts = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    _carregarFeed();
  }

  Future<void> _carregarFeed() async {
    final posts = await _feedService.getFeedGlobal();
    setState(() {
      _posts = List.from(posts); // Cria uma nova lista para forçar a UI a ver a diferença
      _carregando = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_carregando) {
      return const Center(child: CircularProgressIndicator(color: Colors.deepPurple));
    }

    if (_posts.isEmpty) {
      return const Center(child: Text('Nenhuma publicação encontrada.', style: TextStyle(fontSize: 16)));
    }

    return RefreshIndicator(
      onRefresh: _carregarFeed,
      color: Colors.deepPurple,
      child: ListView.builder(
        itemCount: _posts.length,
        itemBuilder: (context, index) {
          return PostCard(
            post: _posts[index],
            // NOVO: Quando o card avisar que foi deletado, recarregamos a tela
            onPostDeletado: () {
              _carregarFeed(); 
            },
          );
        },
      ),
    );
  }
}