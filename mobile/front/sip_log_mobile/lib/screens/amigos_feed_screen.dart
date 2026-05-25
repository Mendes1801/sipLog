import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/feed_response_model.dart';
import '../services/http_feed_service.dart';
import '../widgets/post_card.dart';

class AmigosFeedScreen extends StatefulWidget {
  const AmigosFeedScreen({super.key});

  @override
  State<AmigosFeedScreen> createState() => _AmigosFeedScreenState();
}

class _AmigosFeedScreenState extends State<AmigosFeedScreen> {
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
      final posts = await feedService.getFeedAmigos();
      if (mounted) {
        setState(() {
          _posts = posts;
          _carregando = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _carregando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar feed de amigos: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_carregando) {
      return Center(child: CircularProgressIndicator(color: Theme.of(context).colorScheme.primary));
    }

    if (_posts.isEmpty) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.people_outline, size: 64, color: Colors.grey),
              SizedBox(height: 10),
              Text(
                'Nenhuma publicação dos seus amigos ainda. Comece a seguir pessoas para ver o que elas estão bebendo!',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
            ],
          ),
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _carregarFeed,
      color: Theme.of(context).colorScheme.primary,
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
