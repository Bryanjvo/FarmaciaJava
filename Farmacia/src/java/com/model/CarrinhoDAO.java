/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

import com.controller.Carrinho;
import com.controller.ItemCarrinho;
import com.controller.Produtos;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author bryan
 */
public class CarrinhoDAO extends DAO{
    public void adicionarAoCarrinho(Carrinho carrinho) {
        try {
            abrirBanco();
            // Verifica se o produto já está no carrinho
            String selectSQL = "SELECT quantidade FROM carrinho WHERE id_cliente = ? AND id_produto = ?";
            pst = con.prepareStatement(selectSQL);
            pst.setInt(1, carrinho.getId_cliente());
            pst.setInt(2, carrinho.getId_produto());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Atualiza quantidade se já existir
                int novaQuantidade = rs.getInt("quantidade") + carrinho.getQuantidade();
                String updateSQL = "UPDATE carrinho SET quantidade = ? WHERE id_cliente = ? AND id_produto = ?";
                pst = con.prepareStatement(updateSQL);
                pst.setInt(1, novaQuantidade);
                pst.setInt(2, carrinho.getId_cliente());
                pst.setInt(3, carrinho.getId_produto());
                pst.executeUpdate();
            } else {
                // Insere novo item
                String insertSQL = "INSERT INTO carrinho (id_cliente, id_produto, quantidade) VALUES (?, ?, ?)";
                pst = con.prepareStatement(insertSQL);
                pst.setInt(1, carrinho.getId_cliente());
                pst.setInt(2, carrinho.getId_produto());
                pst.setInt(3, carrinho.getQuantidade());
                pst.executeUpdate();
            }

            fecharBanco();
        } catch (Exception e) {
            System.out.println("Erro ao adicionar ao carrinho: " + e.getMessage());
        }
    }
    
    public List<ItemCarrinho> listarCarrinho(Carrinho carrinho) {
        List<ItemCarrinho> itens = new ArrayList<>();

        try {
            abrirBanco();
            String sql = "SELECT c.id_produto, c.quantidade, p.nome, p.preco, p.imagem, p.receita, p.estoque "
                    + "FROM carrinho c INNER JOIN produtos p ON c.id_produto = p.id WHERE c.id_cliente = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, carrinho.getId_cliente());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Produtos prod = new Produtos();
                prod.setId(rs.getInt("id_produto"));
                prod.setNome(rs.getString("nome"));
                prod.setPreco(rs.getDouble("preco"));
                prod.setImagem(rs.getString("imagem"));
                prod.setReceita(rs.getBoolean("receita"));
                prod.setEstoque(rs.getInt("estoque"));

                int quantidade = rs.getInt("quantidade");
                ItemCarrinho item = new ItemCarrinho(prod, quantidade);
                itens.add(item);
            }

            fecharBanco();
        } catch (Exception e) {
            System.out.println("Erro ao listar carrinho: " + e.getMessage());
        }

        return itens;
    }
    
    public void limparCarrinho(int idCliente) {
        try {
            abrirBanco();
            String sql = "DELETE FROM carrinho WHERE id_cliente = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, idCliente);
            pst.executeUpdate();
            fecharBanco();
        } catch (Exception e) {
            System.out.println("Erro ao limpar carrinho: " + e.getMessage());
        }
    }
    
    public void diminuirProdutoCarrinho(Carrinho carrinho){
        try {
            abrirBanco();
            String selectSQL = "SELECT quantidade FROM carrinho WHERE id_cliente = ? AND id_produto = ?";
            pst = con.prepareStatement(selectSQL);
            pst.setInt(1, carrinho.getId_cliente());
            pst.setInt(2, carrinho.getId_produto());
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int novaQuantidade = rs.getInt("quantidade") - carrinho.getQuantidade();
                String updateSQL = "UPDATE carrinho SET quantidade = ? WHERE id_cliente = ? AND id_produto = ?";
                pst = con.prepareStatement(updateSQL);
                pst.setInt(1, novaQuantidade);
                pst.setInt(2, carrinho.getId_cliente());
                pst.setInt(3, carrinho.getId_produto());
                pst.executeUpdate();
            }
            
            fecharBanco();
        } catch (Exception e) {
            System.out.println("Erro ao diminuir do carrinho: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void excluirProdutoCarrinho(Carrinho carrinho) {
        try {
            abrirBanco();
            String deleteSQL = "DELETE FROM carrinho WHERE id_cliente = ? AND id_produto = ?";
            pst = con.prepareStatement(deleteSQL);
            pst.setInt(1, carrinho.getId_cliente());
            pst.setInt(2, carrinho.getId_produto());
            pst.executeUpdate();
            fecharBanco();
        } catch (Exception e) {
            System.out.println("Erro ao remover produto do carrinho: " + e.getMessage());
        }
    }
}
