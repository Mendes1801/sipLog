import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../services/auth_service.dart';


class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);

    return Scaffold(
      backgroundColor: Colors.deepPurple,
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'SipLog',
                style: TextStyle(
                  fontFamily: 'BaksoSapi',
                  fontSize: 64,
                  color: Colors.white,
                ),
              ),
              const SizedBox(height: 20),
              const Text(
                'Registre suas experiências com bebidas de forma fácil e social.',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.white70, fontSize: 16),
              ),
              const SizedBox(height: 60),
              if (authService.isBusy)
                const CircularProgressIndicator(color: Colors.white)
              else
                ElevatedButton(
                  onPressed: () => authService.login(),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.white,
                    foregroundColor: Colors.deepPurple,
                    padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 15),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
                  ),
                  child: const Text(
                    'Entrar',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                ),
                const SizedBox(height: 15),
                TextButton(
                  onPressed: () async {
                    final Uri url = Uri.parse('http://ec2-52-2-112-100.compute-1.amazonaws.com:8080/realms/BFF/protocol/openid-connect/registrations?client_id=sipLog&response_type=code&scope=openid%20profile%20email&redirect_uri=com.example.siplogmobile://oauth2redirect');
                    if (await canLaunchUrl(url)) {
                      await launchUrl(url, mode: LaunchMode.externalApplication);
                    }
                  },
                  child: const Text('Criar conta', style: TextStyle(color: Colors.white70, fontSize: 16)),
                ),

            ],
          ),
        ),
      ),
    );
  }
}
