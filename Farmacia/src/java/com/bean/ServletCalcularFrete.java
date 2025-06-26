package com.bean;

import com.controller.Frete;
import com.controller.ResultadoFrete;
import okhttp3.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import com.google.gson.JsonObject; // Importe para lidar com objetos JSON
import com.google.gson.JsonParser; // Importe para analisar o JSON como um elemento genérico
import com.google.gson.JsonArray; // Importe para lidar com arrays JSON
import java.util.Comparator; // Importe para usar o comparator

@WebServlet("/calcular-frete")
public class ServletCalcularFrete extends HttpServlet {

    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5NTYiLCJqdGkiOiIwOGU3NGQxNWUyNzg5OTA3MmVhYjA0MzU2ZDRiYmFmZDAxOGJjNjAxZDg0MGM1YTQ4NGRmZGJiNGY5ZDY4MTJmYzY5NmI5MmZlOTZhNzY2NSIsImlhdCI6MTc1MDExNjExNy4wNzUzOTQsIm5iZiI6MTc1MDExNjExNy4wNzUzOTgsImV4cCI6MTc4MTY1MjExNy4wNjU4MDgsInN1YiI6IjlmMjlhYmRjLTllYTEtNDNjNC1iM2NiLWFmZTViYjYwNmYxZCIsInNjb3BlcyI6WyJzaGlwcGluZy1jYWxjdWxhdGUiXX0.cDyrm6BrY8eVGt31ZpFZG-yhZIjQY02IFN2HQ87KATn-jnDQAPNPBDqI1N774aEs4rBiYjW6jFN779Qeeq5VQjVhcEJPe677LzPWh3pNl4k543RUb6mPZsUUIISeAJgrmllJteoyQxD2ADcEzAkkBTjrZx2zim4mlsCgRZoKLtloNUWazaGQCLQUef-T4ATB6cNaOdsKISk38IL5kBhRnfPVzJ3YwBcbtpkMbpGoJmiZwmTi6DrwRllZu2eXiMnXGf0ZT6tekJlPZai8b55Igpi4PTcBe8A9IMI1yrMMduHaixg4aaGgBXJzhpYiJOF63WNs9j1rtDLYoPkqkcXmwoVjpuivDcocwvTa8rgKpx-9vl8Nbkq-yKRjETf302HNmPDhZYO9gA2qyn8YcXl6NGxXfnTUR-gEa4DosgvWUNyBhdm5X_BEgyaY6agZjW5JYK4_dtfuwn8wBPAtQ3-WpAM8WjnUdNCKcZQMeujgSkLq_fO-LFOqHoz5QJ4GmnmxJC-YsU-JRa7MwxeVQv6FohmiZiNDnNQcaCGwyBxFkHoIshgKNOCkslRoxDc9GaxYn9RrSbU3bQmnPouhrxn7NX6N40jy07C6xGJwlFKQ-nFh3sCC4mo-Ifo50HFaMHaZvfUzaH4IzxOdKTXoeMSE5YIdEYRp1bFprKttudVv16w"; // Substitua pelo seu token

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String cepDestino = request.getParameter("cep");
        System.out.println("CEP recebido no servlet: " + cepDestino);

        if (cepDestino == null || cepDestino.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CEP não informado.");
            return;
        }

        // --- Início da configuração para pular a verificação SSL (NÃO RECOMENDADO EM PRODUÇÃO) ---
        OkHttpClient client;
        try {
            // Cria um TrustManager que não verifica certificados
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Não faz nada, aceita todos os clientes
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Não faz nada, aceita todos os servidores
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
            };

            // Instala o TrustManager que não verifica
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true) // Aceita qualquer hostname
                    .build();

        } catch (Exception e) {
            System.err.println("Erro ao configurar SSL: " + e.getMessage());
            client = new OkHttpClient(); // Fallback para cliente padrão
        }
        // --- Fim da configuração para pular a verificação SSL ---

        String jsonBody = "{"
                + "\"from\": { \"postal_code\": \"70002900\" },"
                + "\"to\": { \"postal_code\": \"" + cepDestino + "\" },"
                + "\"products\": [{"
                + "    \"width\": 15,"
                + "    \"height\": 10,"
                + "    \"length\": 20,"
                + "    \"weight\": 1.0,"
                + "    \"quantity\": 1"
                + "}],"
                // REMOVEMOS A PARTE "services": ["1"] PARA PEGAR TODAS AS OPÇÕES
                + "\"options\": {"
                + "    \"insurance_value\": 50,"
                + "    \"receipt\": false,"
                + "    \"own_hand\": false"
                + "}"
                + "}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Request req = new Request.Builder()
                .url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response apiResponse = client.newCall(req).execute()) {
            String json = apiResponse.body().string(); // Obtenha a string JSON primeiro
            System.out.println("Resposta da API Melhor Envio: " + json); // Log para depuração

            if (apiResponse.isSuccessful()) {
                Gson gson = new Gson();
                com.google.gson.JsonElement jsonElement = JsonParser.parseString(json);

                if (jsonElement.isJsonArray()) {
                    Type listType = new TypeToken<List<ResultadoFrete>>() {
                    }.getType();
                    List<ResultadoFrete> fretes = gson.fromJson(json, listType);

                    // Lógica para encontrar o frete mais barato válido
                    ResultadoFrete freteMaisBarato = null;

                    for (ResultadoFrete frete : fretes) {
                        // Verifica se o frete não tem erro e tem um preço válido
                        if (frete.getError() == null || frete.getError().isEmpty()) {
                            try {
                                double precoAtual = Double.parseDouble(frete.getPrice());
                                if (freteMaisBarato == null || precoAtual < Double.parseDouble(freteMaisBarato.getPrice())) {
                                    freteMaisBarato = frete;
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Erro ao converter preço do frete '" + frete.getName() + "': " + frete.getPrice());
                            }
                        }
                    }

                    Frete frete = new Frete();
                    if (freteMaisBarato != null) {
                        request.setAttribute("valorFrete", String.format("%.2f", Double.parseDouble(freteMaisBarato.getPrice())));
                        request.setAttribute("prazoEntrega", freteMaisBarato.getDelivery_time());
                        frete.setPreco(Double.valueOf(freteMaisBarato.getPrice()));
                        // Salva na sessão também:
                        HttpSession session = request.getSession();
                        session.setAttribute("fretePreco", String.format("%.2f", Double.parseDouble(freteMaisBarato.getPrice())));
                        session.setAttribute("fretePrazo", freteMaisBarato.getDelivery_time());
                        System.out.println("Frete mais barato encontrado: " + freteMaisBarato.getName() + " - R$ " + freteMaisBarato.getPrice() + " - " + freteMaisBarato.getDelivery_time() + " dias.");
                    } else {
                        // Se todos os serviços retornaram erro ou não há serviços válidos
                        request.setAttribute("valorFrete", "Indisponível");
                        request.setAttribute("prazoEntrega", "N/A");
                        System.out.println("Nenhum serviço de frete válido encontrado.");
                    }

                } else if (jsonElement.isJsonObject()) {
                    JsonObject errorObject = jsonElement.getAsJsonObject();
                    // Aqui pode haver um campo "message" ou "error" dependendo do tipo de erro
                    String errorMessage = "Erro desconhecido da API";
                    if (errorObject.has("message")) {
                        errorMessage = errorObject.get("message").getAsString();
                    } else if (errorObject.has("error")) {
                        errorMessage = errorObject.get("error").getAsString();
                    }
                    request.setAttribute("valorFrete", "Erro: " + errorMessage);
                    request.setAttribute("prazoEntrega", "Não calculado");
                    System.err.println("Erro da API Melhor Envio: " + errorMessage);
                } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                    String errorMessage = jsonElement.getAsString();
                    request.setAttribute("valorFrete", "Erro: " + errorMessage);
                    request.setAttribute("prazoEntrega", "Não calculado");
                    System.err.println("Resposta da API (string de erro): " + errorMessage);
                } else {
                    request.setAttribute("valorFrete", "Erro de Formato");
                    request.setAttribute("prazoEntrega", "Não calculado");
                    System.err.println("Resposta da API com formato inesperado: " + json);
                }

            } else {
                System.err.println("Erro na API (código HTTP " + apiResponse.code() + "): " + json);
                request.setAttribute("valorFrete", "Erro HTTP: " + apiResponse.code());
                request.setAttribute("prazoEntrega", "Não calculado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("valorFrete", "Erro interno");
            request.setAttribute("prazoEntrega", "Não calculado");
        }

        request.getRequestDispatcher("carrinho.jsp").forward(request, response);
    }
}
