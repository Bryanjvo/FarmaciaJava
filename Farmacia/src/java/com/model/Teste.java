/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;


import java.sql.SQLException;

/**
 *
 * @author bryan
 */
public class Teste {
    public static void main(String[] args) throws SQLException {
//        Login login = new Login();
//        login.setEmail("testeteste@email.com");
//        login.setSenha("12345");
//        
//        LoginDAO loginDAO = new LoginDAO();
//        loginDAO.inserir(login);
        DAO c = new DAO();
        c.abrirBanco();
    }
}
