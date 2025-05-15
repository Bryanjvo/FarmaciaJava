<%-- 
    Document   : perfil
    Created on : May 12, 2025, 4:45:36 PM
    Author     : bryan
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%

    if (session == null || session.getAttribute("id") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DrogaBryan</title>
    <link rel="icon" type="image/x-icon" href="../design_&_layout/logotipo/drogabryan.png">
    <link rel="stylesheet" href="assets/css/perfil.css">
</head>

<body>
    <header>
        <nav>
            <ul>
                <li><a href="index.jsp">Home</a></li>
                <li><a href="produtos.jsp">Produtos</a></li>
                <li><a href="login.jsp">Login</a></li>
                <li><a href="cadcliente.jsp">Cadastro</a></li>
                <li><a href="perfil.jsp">Meu Perfil</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <section>
            <div class="form">
                <h1>Perfil de Cliente</h1>
                <br>
                <h4 class="dados">Seus Dados:</h4>
                <br>
                
                <p>Seu nome: <%= session.getAttribute("nome") %> </p>
                <p>Seu e-mail: <%= session.getAttribute("email") %> </p>
                <p>Seu endereço: <%= session.getAttribute("endereco") %> </p>

                <br>
                <h4 class="opcoes">Opções</h4>
                <br>
                <a href="AlterarCliente.php?idex=<?php echo $_SESSION['id_usuario']; ?>">Alterar Conta</a>
                <a href="ExcluirCliente.php?idex=<?php echo $_SESSION['id_usuario']; ?>">Excluir Conta</a>
                <a href="CadCliente.php">Cadastrar Nova Conta</a>
                <a href="ListarPedidos.php">Meus Pedidos</a>
            </div>
        </section>
    </main>

    <footer>
        <div class="footer-content">
            <ul class="autores">
                <h3>Autor</h3>
                <li><img class="autoresImg" src="../design_&_layout/logotipo/github-mark.png">
                    <a href="https://github.com/Bryanjvo">Bryan</a>
                </li>
            </ul>
            <ul>
                <h3>Contato</h3>
                <li><img class="autoresImg" src="../design_&_layout/logotipo/telefone.png"> (61) 91234-5678</li>
                <li><img class="autoresImg" src="../design_&_layout/logotipo/email.png">
                    <a style="color: white;" href="mailto:#">drogabryan@gmail.com</a>
                </li>
            </ul>
            <ul>
                <h3>Endereço</h3>
                <li>CEP: 260.333-299</li>
                <li>CNB 10</li>
                <li>Taguatinga - Brasília/DF</li>
            </ul>
            <ul>
                <h3>Redes Sociais</h3>
                <li><img class="autoresImg" src="../design_&_layout/logotipo/ig icon.png"> <a href="#">Instagram</a></li>
                <li><img class="autoresImg" src="../design_&_layout/logotipo/whatsapp.png"> <a href="#">WhatsApp</a></li>
            </ul>
        </div>
    </footer>

</body>

</html>
 
