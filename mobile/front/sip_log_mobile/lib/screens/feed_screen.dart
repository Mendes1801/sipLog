import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../services/http_feed_service.dart';
import '../widgets/post_card.dart';

class FeedScreen extends StatefulWidget {
  const FeedScreen({super.key});

  @override
  State<FeedScreen> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {
  List<FeedResponseModel> _posts = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _carregarFeed();
    });
  }

  Future<void> _carregarFeed() async {
    final feedService = Provider.of<HttpFeedService>(context, listen: false);
    try {
      final posts = await feedService.getFeedGlobal();
      if (mounted) {
        setState(() {
          _posts = List.from(posts);
          _carregando = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar feed: $e')),
        );
      }
    }
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
            onPostDeletado: () {
              _carregarFeed(); 
            },
          );
        },
      ),
    );
  }
}
