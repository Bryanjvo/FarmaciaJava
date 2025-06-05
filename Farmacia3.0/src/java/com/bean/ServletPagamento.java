package com.bean;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.controller.Carrinho;
import com.controller.ItemCarrinho;
import com.model.CarrinhoDAO;
import okhttp3.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@WebServlet("/pagar")
public class ServletPagamento extends HttpServlet {
    private static final String ACCESS_TOKEN = "APP_USR-503418991994651-052312-d0acd30f0927106e87c52af4e050a344-2454289616";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Recupera o ID do cliente
        HttpSession session = request.getSession(false);
        Integer idCliente = (session != null) 
            ? (Integer) session.getAttribute("id") 
            : null;

        if (idCliente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Busca itens do carrinho no banco
        Carrinho carrinhoBean = new Carrinho();
        carrinhoBean.setId_cliente(idCliente);
        CarrinhoDAO dao = new CarrinhoDAO();
        List<ItemCarrinho> lista = dao.listarCarrinho(carrinhoBean);

        if (lista.isEmpty()) {
            request.setAttribute("erro", "Seu carrinho está vazio!");
            request.getRequestDispatcher("carrinho.jsp").forward(request, response);
            return;
        }

        // 3. Monta o JSON dinamicamente
        JsonArray itemsArray = new JsonArray();
        for (ItemCarrinho item : lista) {
            JsonObject obj = new JsonObject();
            obj.addProperty("title", item.getProduto().getNome());
            obj.addProperty("quantity", item.getQuantidade());
            obj.addProperty("currency_id", "BRL");
            obj.addProperty("unit_price", item.getProduto().getPreco());
            itemsArray.add(obj);
        }
        JsonObject preference = new JsonObject();
        preference.add("items", itemsArray);
        preference.add("back_urls", new JsonParser().parse("""
  {
    "success": "https://89d6-45-185-42-126.ngrok-free.app/Farmacia/confirmacao",
    "failure": "https://89d6-45-185-42-126.ngrok-free.app/Farmacia/carrinho.jsp",
    "pending": "https://89d6-45-185-42-126.ngrok-free.app/Farmacia/carrinho.jsp"
  }
""").getAsJsonObject());
        preference.addProperty("auto_return", "approved");

        // 4. Configura client “trust-all” (ambiente de sandbox ou testes)
        OkHttpClient client;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {}
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory sf = sc.getSocketFactory();
            client = new OkHttpClient.Builder()
                .sslSocketFactory(sf, (X509TrustManager)trustAllCerts[0])
                .hostnameVerifier((h, s) -> true)
                .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new ServletException("Erro ao configurar SSL", e);
        }

        // 5. Envia requisição à API do Mercado Pago
        String json = preference.toString();
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"), json);
        Request req = new Request.Builder()
            .url("https://api.mercadopago.com/checkout/preferences")
            .post(body)
            .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
            .build();

        try (Response res = client.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                throw new IOException("Erro na API MP: " + res);
            }
            JsonObject mpResp = JsonParser.parseString(res.body().string()).getAsJsonObject();
            String initPoint = mpResp.get("init_point").getAsString();
            response.sendRedirect(initPoint);
        }
    }
}
