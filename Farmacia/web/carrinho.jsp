<%-- 
    Document   : produtos
    Created on : May 12, 2025, 8:22:27 PM
    Author     : bryan
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.controller.ItemCarrinho" %>
<%@ page import="com.controller.Carrinho" %>
<%@ page import="com.model.CarrinhoDAO" %>

<%
    // Recupera o ID do cliente da sessão
    Integer idCliente = (Integer) session.getAttribute("id");

    ArrayList<ItemCarrinho> listaCarrinho = new ArrayList<>();

    if (idCliente != null) {
        Carrinho carrinho = new Carrinho();
        carrinho.setId_cliente(idCliente);

        CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
        listaCarrinho = (ArrayList<ItemCarrinho>) carrinhoDAO.listarCarrinho(carrinho);
    } else {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DrogaBryan - Carrinho de Compras</title>
    <link rel="stylesheet" href="assets/css/carrinho.css">
    <link rel="icon" type="image/x-icon" href="../design_&_layout/logotipo/drogabryan.png">
</head>
<body>
    <header>
        <nav>
            <ul>
                <li><a href="index.jsp">Home</a></li>
                <li><a href="produtos.jsp">Produtos</a></li>
                <%-- Verifica se o usuário está logado, baseado em uma variável de sessão, por exemplo "nome" --%>
                <%
                    if (session.getAttribute("nome") == null) {
                %>
                    <li><a href="../../viewCliente/Login.jsp">Login</a></li>
                    <li><a href="../../viewCliente/CadCliente.jsp">Cadastro</a></li>
                <%
                    } else { 
                %>
                    <li><a href="carrinho.jsp">Carrinho</a></li>
                    <li><a href="pedidos.jsp">Pedidos</a></li>
                    <li><a href="perfil.jsp">Meu Perfil</a></li>
                    <li><a href="logout">Logout</a></li>
                <%
                    }
                %>
            </ul>
        </nav>
    </header>

    <main>
        <div>
            <h1>Carrinho de Compras</h1>
            <section class="lista-carrinho">
                <%
                    double total = 0.0;
                    // Laço para iterar pelos produtos e exibi-los
                    for (ItemCarrinho itemcarrinho : listaCarrinho) {
                    double subtotal = itemcarrinho.getSubtotal();
                    total += subtotal;
                %>
                    <div class="item">
                        <%-- Altere o src abaixo para utilizar a imagem real, se a classe possuir essa informação --%>
                        <img class="imgProduto" src="<%= itemcarrinho.getProduto().getImagem() %>" alt="Imagem do Produto">
                        <%-- <h3>ID: <%= produto.getId() %></h3> --%>
                        <h3>Nome: <%= itemcarrinho.getProduto().getNome() %></h3>
                        <p>Preço: R$<%= itemcarrinho.getProduto().getPreco() %></p>
                        <p>Quantidade: <%= itemcarrinho.getQuantidade() %></p>
                        <p>Subtotal: R$<%= String.format("%.2f", subtotal) %></p>
                        <p>Receita: <%= itemcarrinho.getProduto().isReceita() ? "Sim" : "Não" %></p>
                        <%-- Link para efetuar a compra/pedido do produto (ajuste o caminho conforme sua estrutura) --%>
                        <a href="AdicionarCarrinho?idproduto=<%= itemcarrinho.getProduto().getId() %>&quantidade=1" class="comprarButton">
                                <img src="assets/img/mais.png" alt="Adicionar" style="width: 32px; height: 32px;">
                        </a>
                        <% if(itemcarrinho.getQuantidade() > 1){ %>
                        <a href="DiminuirProdutoCarrinho?idproduto=<%= itemcarrinho.getProduto().getId() %>&quantidade=1" class="comprarButton">
                                <img src="assets/img/menos.png" alt="Adicionar" style="width: 32px; height: 32px;">
                        </a>        
                        <% }else{ %>
                        <a href="ExcluirProdutoCarrinho?idproduto=<%= itemcarrinho.getProduto().getId() %>&quantidade=1" class="comprarButton">
                                <img src="assets/img/lixeira.png" alt="Adicionar" style="width: 32px; height: 32px;">
                        </a>  
                        <% } %>
                        
                        
                        
                    </div>
                <%
                    }
                %>
                <form id="freteForm">
                    <input type="text" name="cep" placeholder="Digite seu CEP" required>
                    <button type="submit">Calcular Frete</button>
                </form>

                <div id="resultadoFrete"></div>

                <script>
                    document.getElementById("freteForm").addEventListener("submit", function(event) {
                        event.preventDefault();

                        const formData = new FormData(this);

                        fetch("<%= request.getContextPath() %>/calcular-frete", {
                            method: "POST",
                            body: formData
                        })
                        .then(res => res.json())
                        .then(data => {
                            console.log("Resposta recebida:", data);
                            document.getElementById("resultadoFrete").innerHTML =
                                `Frete: R$ ${data.valorFrete} - Prazo: ${data.prazoEntrega} dia(s)`;
                        })
                        .catch(err => {
                            document.getElementById("resultadoFrete").innerText = "Erro ao calcular frete.";
                            console.error(err);
                        });
                    });
                </script>

            </section>
            <!-- SEÇÃO DE TOTAL E FINALIZAÇÃO -->
            <section class="total-carrinho">
                <h2>Total da Compra: R$<%= String.format("%.2f", total) %></h2>

                <form action="pagar" method="post">
                    <button type="submit" class="finalizarButton">
                        Finalizar Compra
                    </button>
                </form>
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
