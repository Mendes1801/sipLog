// WineAPI Documentation: https://wineapi.io/docs/description/introduction
// API Key: wapi_c4bb5b3c0109db7cfe7e21aaa68bb1444ca728a02bd12b2157c798fd022c8bd1

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONObject;

public class API_Wineapi {
    
    private static final String API_KEY = "wapi_c4bb5b3c0109db7cfe7e21aaa68bb1444ca728a02bd12b2157c798fd022c8bd1";
    private static final String BASE_URL = "https://api.wineapi.io";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Traduz um texto para português-PT usando MyMemory API
     * API Gratuita - Sem necessidade de autenticação
     */
    public static String traduzirParaPortugues(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        
        try {
            // Limita o tamanho para evitar erro - MyMemory tem limite de caracteres
            String textoLimitado = texto.length() > 500 ? texto.substring(0, 500) : texto;
            
            String urlEncoded = URLEncoder.encode(textoLimitado, StandardCharsets.UTF_8.toString());
            String endpoint = "https://api.mymemory.translated.net/get?q=" + urlEncoded + "&langpair=en|pt-PT";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                return json.getJSONObject("responseData").getString("translatedText");
            }
        } catch (Exception e) {
            System.err.println("Erro ao traduzir: " + e.getMessage());
        }
        
        return texto; // Retorna original se não conseguir traduzir
    }


    //01. Pesquisa por vinhos na API
    
    public static void pesquisarVinhos(String query) {
        try {
            System.out.println("\n=== PESQUISANDO VINHOS ===");
            System.out.println("Query: " + query);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/wines/search?q=" + query))
                .header("X-API-Key", API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            String respostaOriginal = response.body();
            
            // Traduz a resposta para português
            String respostaTraduzida = traduzirParaPortugues(respostaOriginal);
            System.out.println("Resposta (EN): " + respostaOriginal);
            System.out.println("Resposta (PT-PT): " + respostaTraduzida);
        } catch (Exception e) {
            System.err.println("Erro ao pesquisar vinhos: " + e.getMessage());
        }
    }

    //02. Obter detalhes de um vinho

    public static void obterDetalhesVinho(String vinhoId) {
        try {
            System.out.println("\n=== DETALHES DO VINHO ===");
            System.out.println("ID: " + vinhoId);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/wines/" + vinhoId))
                .header("X-API-Key", API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Resposta: " + response.body());
        } catch (Exception e) {
            System.err.println("Erro ao obter detalhes do vinho: " + e.getMessage());
        }
    }

    //03. Obter pareamentos de um vinho

    public static void obterPareamentosVinho(String vinhoId) {
        try {
            System.out.println("\n=== PAREAMENTOS DO VINHO ===");
            System.out.println("ID: " + vinhoId);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/wines/" + vinhoId + "/pairings"))
                .header("X-API-Key", API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Resposta: " + response.body());
        } catch (Exception e) {
            System.err.println("Erro ao obter pareamentos: " + e.getMessage());
        }
    }

    //04. Obter todas as regiões de vinhos disponíveis

    public static void obterRegioes() {
        try {
            System.out.println("\n=== REGIÕES DE VINHO ===");
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/regions"))
                .header("X-API-Key", API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Resposta: " + response.body());
        } catch (Exception e) {
            System.err.println("Erro ao obter regiões: " + e.getMessage());
        }
    }

    //05. Obter variedades de uva

    public static void obterUvas() {
        try {
            System.out.println("\n=== VARIEDADES DE UVA ===");
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/grapes"))
                .header("X-API-Key", API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Resposta: " + response.body());
        } catch (Exception e) {
            System.err.println("Erro ao obter uvas: " + e.getMessage());
        }
    }

   

    

    
    
}


