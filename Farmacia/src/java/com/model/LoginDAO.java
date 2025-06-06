/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import com.controller.Login;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;

public class LoginDAO extends DAO {

    public boolean inserir(Login login) {
    try {
        abrirBanco();
        String senhaCriptografada = BCrypt.hashpw(login.getSenha(), BCrypt.gensalt());
        String query = "INSERT INTO clientes (nome, email, senha, endereco) VALUES(?, ?, ?, ?)";
        pst = con.prepareStatement(query);
        pst.setString(1, login.getNome());
        pst.setString(2, login.getEmail());
        pst.setString(3, senhaCriptografada);
        pst.setString(4, login.getEndereco());

        int linhasAfetadas = pst.executeUpdate(); // Retorna número de linhas afetadas

        fecharBanco();
        return linhasAfetadas > 0; // Se inseriu, retorna true
    } catch (Exception e) {
        System.out.println("Erro ao inserir cliente: " + e.getMessage());
        return false;
    }
}

    public Login pesquisar(Login login) {
        Login loginBuscado = new Login();

        try {
            abrirBanco();
            String query = "SELECT id, nome, email, senha, endereco FROM clientes WHERE email = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, login.getEmail());

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String senhaHashBanco = rs.getString("senha");

                // Verificar se a senha informada corresponde ao hash armazenado
                if (BCrypt.checkpw(login.getSenha(), senhaHashBanco)) {
                    loginBuscado = new Login();
                    loginBuscado.setId(rs.getInt("id"));
                    loginBuscado.setNome(rs.getString("nome"));
                    loginBuscado.setEmail(rs.getString("email"));
                    loginBuscado.setEndereco(rs.getString("endereco"));
                    // você pode adicionar mais campos se precisar
                }
            }

            fecharBanco();
        } catch (Exception e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }

        return loginBuscado;
    }
}
