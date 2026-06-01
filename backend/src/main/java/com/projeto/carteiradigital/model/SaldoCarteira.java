package com.projeto.carteiradigital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "saldo_carteira")
@IdClass(SaldoCarteiraId.class) 
public class SaldoCarteira {

    @Id
    @ManyToOne
    @JoinColumn(name = "endereco_carteira", nullable = false)
    private Carteira carteira;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_moeda", nullable = false)
    private Moeda moeda;

    @Column(name = "saldo", precision = 36, scale = 18, nullable = false)
    private BigDecimal saldo;

   
    @Column(name = "data_atualizacao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataAtualizacao;

    public SaldoCarteira() {
    }

    public SaldoCarteira(Carteira carteira, Moeda moeda, BigDecimal saldo) {
        this.carteira = carteira;
        this.moeda = moeda;
        this.saldo = saldo;
    }

  
    public Carteira getCarteira() {
        return carteira;
    }

    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }

    public Moeda getMoeda() {
        return moeda;
    }

    public void setMoeda(Moeda moeda) {
        this.moeda = moeda;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    
}