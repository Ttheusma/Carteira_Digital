package com.projeto.carteiradigital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "carteira")
public class Carteira {

   
    @Id
    @Column(name = "endereco_carteira", length = 36, nullable = false, updatable = false)
    private String enderecoCarteira;

    
    @Column(name = "hash_chave_privada", length = 255, nullable = false)
    private String hashChavePrivada;

    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

   
    public Carteira() {
    }

    public Carteira(String enderecoCarteira, String hashChavePrivada, String status) {
        this.enderecoCarteira = enderecoCarteira;
        this.hashChavePrivada = hashChavePrivada;
        this.status = status;
    }

    
    @PrePersist
    protected void onCreate() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }

    // Getters e Setters
    public String getEnderecoCarteira() {
        return enderecoCarteira;
    }

    public void setEnderecoCarteira(String enderecoCarteira) {
        this.enderecoCarteira = enderecoCarteira;
    }

    public String getHashChavePrivada() {
        return hashChavePrivada;
    }

    public void setHashChavePrivada(String hashChavePrivada) {
        this.hashChavePrivada = hashChavePrivada;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carteira carteira = (Carteira) o;
        return Objects.equals(enderecoCarteira, carteira.enderecoCarteira);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enderecoCarteira);
    }
}