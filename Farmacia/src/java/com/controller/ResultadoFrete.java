package com.controller;

public class ResultadoFrete {
    private String id;
    private String name;
    private String price;
    private int delivery_time;
    private String error; // Adicione este campo para mapear o erro da API
    // private String error_message; // Em algumas APIs, pode vir como error_message ou message

    // ... outros campos que você possa ter

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(int delivery_time) {
        this.delivery_time = delivery_time;
    }

    // Novo getter e setter para o campo 'error'
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Se a API retornar "error_message" ou "message" para o erro, adicione também:
    /*
    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
    */
}