<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.model.PedidoDAO, com.controller.Pedido, com.controller.ItemPedido" %>

<%
    Integer idCliente = (Integer) session.getAttribute("id");
    List<Pedido> listaPedidos = new ArrayList<>();

    if (idCliente != null) {
        PedidoDAO pedidoDAO = new PedidoDAO();
        listaPedidos = pedidoDAO.listarPedidos(idCliente);
    } else {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>DrogaBryan - Meus Pedidos</title>
    <link rel="stylesheet" href="assets/css/carrinho.css">
    <link rel="icon" type="image/x-icon" href="../design_&_layout/logotipo/drogabryan.png">
</head>
<body>
<header>
    <nav>
        <ul>
            <li><a href="index.jsp">Home</a></li>
            <li><a href="produtos.jsp">Produtos</a></li>
            <% if (session.getAttribute("nome") == null) { %>
                <li><a href="../../viewCliente/Login.jsp">Login</a></li>
                <li><a href="../../viewCliente/CadCliente.jsp">Cadastro</a></li>
            <% } else { %>
                <li><a href="carrinho.jsp">Carrinho</a></li>
                <li><a href="pedidos.jsp">Pedidos</a></li>
                <li><a href="perfil.jsp">Meu Perfil</a></li>
                <li><a href="logout">Logout</a></li>
            <% } %>
        </ul>
    </nav>
</header>

<main>
    <div>
        <h1>Meus Pedidos</h1>

        <section class="lista-carrinho">
            <% if (listaPedidos.isEmpty()) { %>
                <p>Você ainda não fez nenhum pedido.</p>
            <% } else { 
                for (Pedido pedido : listaPedidos) {
            %>
                <div class="item">
                    <div style="text-align: left;">
                        <h3>Pedido #<%= pedido.getId() %></h3>
                        <p><strong>Data:</strong> <%= pedido.getDataPedido() %></p>
                        <div style="margin-top: 10px;">
                            <% for (ItemPedido item : pedido.getItens()) { %>
                                <p>
                                    <strong><%= item.getNomeProduto() %></strong><br>
                                    Quantidade: <%= item.getQuantidade() %><br>
                                    Subtotal: R$<%= String.format("%.2f", item.getSubtotal()) %>
                                </p>
                            <% } %>
                        <% if(pedido.getFrete() > 0.0){ %>
                        <p><strong>Frete: </strong>R$<%= String.format("%.2f",pedido.getFrete()) %></p>   
                        <% } %>
                        </div>
                        <p><strong>Total do Pedido:</strong> R$<%= String.format("%.2f", pedido.getValorTotal()) %></p>
                    </div>
                </div>
            <% } } %>
        </section>
    </div>
</main>

<footer>
    <div class="footer-content">
        <ul class="autores">
                <h3>Autor</h3>
                <li> <img class="autoresImg" src="assets/img/github-mark.png"> <a href="https://github.com/Bryanjvo">Bryan</a></li>
            </ul>
            <ul>
                <h3>Contato</h3>
                <li> <img class="autoresImg" src="assets/img/telefone.png"> (61) 91234-5678</li>
                <li> <img class="autoresImg" src="assets/img/email.png"> <a style="color: white;" href="mailto:#">drogabryan@gmail.com</a></li>
            </ul>
            <ul>
                <h3>Endereço</h3>
                <li>CEP: 260.333-299</li>
                <li>CNB 10</li>
                <li>Taguatinga - Brasília/DF</li>
            </ul>
            <ul>
                <h3>Redes Sociais</h3>
                <li> <img class="autoresImg" src="assets/img/ig icon.png"> <a href="#">Instagram</a></li>
                <li> <img class="autoresImg" src="assets/img/whatsapp.png"> <a href="#">WhatsApp</a></li>
            </ul>
    </div>
</footer>
</body>
</html>
