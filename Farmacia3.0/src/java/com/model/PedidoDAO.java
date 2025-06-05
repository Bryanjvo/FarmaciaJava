package com.model;

import com.controller.ItemPedido;
import com.controller.Pedido;
import java.sql.*;
import java.util.*;

public class PedidoDAO extends DAO {
    public int criarPedido(int idCliente, double valor_total) throws SQLException, Exception {
        abrirBanco();
        String sql = "INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES (?, NOW(), ?)";
        pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1, idCliente);
        pst.setDouble(2, valor_total);
        pst.executeUpdate();
        ResultSet rs = pst.getGeneratedKeys();
        int idPedido = 0;
        if (rs.next()) idPedido = rs.getInt(1);
        fecharBanco();
        return idPedido;
    }
    
    public List<Pedido> listarPedidos(int idCliente) throws SQLException, Exception {
        List<Pedido> pedidos = new ArrayList<>();
        abrirBanco();

        String sql = """
            SELECT p.id AS pedido_id, p.data_pedido, p.valor_total,
                   pr.nome AS nome_produto, pp.quantidade, pp.subtotal
            FROM pedidos p
            JOIN pedido_produto pp ON p.id = pp.id_pedido
            JOIN produtos pr ON pp.id_produto = pr.id
            WHERE p.id_cliente = ?
            ORDER BY p.id DESC
        """;

        pst = con.prepareStatement(sql);
        pst.setInt(1, idCliente);
        ResultSet rs = pst.executeQuery();

        Map<Integer, Pedido> mapaPedidos = new LinkedHashMap<>();

        while (rs.next()) {
            int idPedido = rs.getInt("pedido_id");
            Pedido pedido = mapaPedidos.get(idPedido);

            if (pedido == null) {
                pedido = new Pedido();
                pedido.setId(idPedido);
                pedido.setDataPedido(rs.getTimestamp("data_pedido"));
                pedido.setValorTotal(rs.getDouble("valor_total"));
                mapaPedidos.put(idPedido, pedido);
            }

            ItemPedido item = new ItemPedido(
                rs.getString("nome_produto"),
                rs.getInt("quantidade"),
                rs.getDouble("subtotal")
            );

            pedido.adicionarItem(item);
        }

        fecharBanco();
        pedidos.addAll(mapaPedidos.values());
        return pedidos;
    }
    
    
    
    
}
