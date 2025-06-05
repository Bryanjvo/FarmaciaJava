package com.bean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@WebServlet("/calcular-frete")
public class ServletCalcularFrete extends HttpServlet {

    // Método para criar um OkHttpClient que ignora a validação SSL,
    // com timeouts aumentados para lidar com conexões lentas.
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // TrustManager que não valida certificados
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                            throws CertificateException { }
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                            throws CertificateException { }
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                        return new java.security.cert.X509Certificate[]{}; 
                    }
                }
            };

            // Configura o SSLContext para usar o trust manager que ignora problemas de certificado
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // Aumenta os timeouts: 30s para conexão e 120s para leitura e escrita
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(120, TimeUnit.SECONDS);
            builder.writeTimeout(120, TimeUnit.SECONDS);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cepDestino = request.getParameter("cep");

        if (cepDestino == null || cepDestino.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CEP não informado");
            return;
        }

        String cepOrigem = "70002900";
        String peso = "1";
        String valorDeclarado = "50";
        String codigoServico = "40010";  // Tente também "04510" se necessário

        String url = "https://ws.correios.com.br/calculador/CalcPrecoPrazo.aspx?"
                + "nCdEmpresa=&sDsSenha="
                + "&sCepOrigem=" + URLEncoder.encode(cepOrigem, "UTF-8")
                + "&sCepDestino=" + URLEncoder.encode(cepDestino, "UTF-8")
                + "&nVlPeso=" + peso
                + "&nCdFormato=1"
                + "&nVlComprimento=20"
                + "&nVlAltura=10"
                + "&nVlLargura=15"
                + "&nVlDiametro=0"
                + "&sCdMaoPropria=n"
                + "&nVlValorDeclarado=" + valorDeclarado
                + "&sCdAvisoRecebimento=n"
                + "&nCdServico=" + codigoServico
                + "&nVlValor=0"
                + "&StrRetorno=xml";

        // Use o OkHttpClient com os timeouts aumentados e SSL inseguro (apenas para desenvolvimento)
        OkHttpClient client = getUnsafeOkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(url)
                .get()
                .build();

        String valorFrete = "";
        String prazoEntrega = "";

        try (Response correiosResponse = client.newCall(httpRequest).execute()) {
            if (correiosResponse.isSuccessful() && correiosResponse.body() != null) {
                String xmlString = correiosResponse.body().string();
                System.out.println("Resposta XML:");
                System.out.println(xmlString);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlString.getBytes("UTF-8")));

                NodeList erroList = doc.getElementsByTagName("Erro");
                if (erroList.getLength() > 0 && !"0".equals(erroList.item(0).getTextContent().trim())) {
                    System.out.println("Erro da API dos Correios: " + erroList.item(0).getTextContent());
                }
                NodeList valorList = doc.getElementsByTagName("Valor");
                NodeList prazoList = doc.getElementsByTagName("PrazoEntrega");

                if (valorList.getLength() > 0 && prazoList.getLength() > 0) {
                    valorFrete = valorList.item(0).getTextContent().trim();
                    prazoEntrega = prazoList.item(0).getTextContent().trim();
                }
            } else {
                System.out.println("Resposta dos Correios mal-sucedida");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{ \"valorFrete\": \"" + valorFrete + "\", \"prazoEntrega\": \"" + prazoEntrega + "\" }");
        out.flush();
    }
}