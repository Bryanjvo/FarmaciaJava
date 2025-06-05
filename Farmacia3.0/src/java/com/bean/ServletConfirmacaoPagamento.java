package com.bean;

import com.controller.Carrinho;
import com.controller.ItemCarrinho;
import com.model.CarrinhoDAO;
import com.model.PedidoDAO;
import com.model.PedidoProdutoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/confirmacao")
public class ServletConfirmacaoPagamento extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1️⃣ Verifica se o pagamento foi aprovado via parâmetro
        String status = request.getParameter("collection_status");  // "approved" ou outro
        if (!"approved".equalsIgnoreCase(status)) {
            response.sendRedirect("carrinho.jsp");
            return;
        }

        // 2️⃣ Recupera ID do cliente
        HttpSession session = request.getSession(false);
        Integer idCliente = (session != null) ? (Integer) session.getAttribute("id") : null;
        if (idCliente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 3️⃣ Busca itens do carrinho
        CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
        Carrinho carrinhoBean = new Carrinho();
        carrinhoBean.setId_cliente(idCliente);
        List<ItemCarrinho> itens = carrinhoDAO.listarCarrinho(carrinhoBean);

        if (itens.isEmpty()) {
            response.sendRedirect("carrinho.jsp");
            return;
        }

        // 4️⃣ Cria Pedido e obtém ID
        PedidoDAO pedidoDAO = new PedidoDAO();
        int idPedido;
        try {
                double total = 0.0;
            for (ItemCarrinho item : itens){
                double subtotal = item.getSubtotal();
                total += subtotal;
            }
            idPedido = pedidoDAO.criarPedido(idCliente, total);

            // 5️⃣ Insere itens em pedido_produto
            PedidoProdutoDAO ppDAO = new PedidoProdutoDAO();
            for (ItemCarrinho item : itens) {
                ppDAO.adicionarItem(idPedido,
                        item.getProduto().getId(),
                        item.getQuantidade(),
                        item.getSubtotal());
            }
            
            
            // 6️⃣ Limpa carrinho
            carrinhoDAO.limparCarrinho(idCliente);

            // 7️⃣ Redireciona para página de pedidos
            response.sendRedirect("pedidos.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("erro.jsp");
        } catch (Exception ex) {
            Logger.getLogger(ServletConfirmacaoPagamento.class.getName()).log(Level.SEVERE, null, ex);
            response.sendRedirect("erro.jsp");
        }

    }
}
