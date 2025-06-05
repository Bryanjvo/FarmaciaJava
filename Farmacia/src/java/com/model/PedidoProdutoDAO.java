package com.model;

import java.sql.*;

public class PedidoProdutoDAO extends DAO {
    public void adicionarItem(int idPedido, int idProduto, int quantidade, double subtotal) throws SQLException, Exception {
        abrirBanco();
        String sql = "INSERT INTO pedido_produto (id_pedido, id_produto, quantidade, subtotal) VALUES (?, ?, ?, ?)";
        pst = con.prepareStatement(sql);
        pst.setInt(1, idPedido);
        pst.setInt(2, idProduto);
        pst.setInt(3, quantidade);
        pst.setDouble(4, subtotal);
        pst.executeUpdate();
        fecharBanco();
    }
}
