package com.projeto.carteiradigital.model;

import java.time.LocalDateTime;

public class Carteira {

    private String enderecoCarteira;
    private String hashChavePrivada;
    private LocalDateTime dataCriacao;
    private String status;

    public Carteira() {
    }

    public Carteira(String enderecoCarteira, String hashChavePrivada, String status) {
        this.enderecoCarteira = enderecoCarteira;
        this.hashChavePrivada = hashChavePrivada;
        this.status = status;
        this.dataCriacao = LocalDateTime.now(); // Definimos direto no construtor agora
    }

    // Getters e Setters
    public String getEnderecoCarteira() { return enderecoCarteira; }
    public void setEnderecoCarteira(String enderecoCarteira) { this.enderecoCarteira = enderecoCarteira; }
    public String getHashChavePrivada() { return hashChavePrivada; }
    public void setHashChavePrivada(String hashChavePrivada) { this.hashChavePrivada = hashChavePrivada; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}