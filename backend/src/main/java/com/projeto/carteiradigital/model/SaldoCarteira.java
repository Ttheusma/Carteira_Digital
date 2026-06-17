package com.projeto.carteiradigital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaldoCarteira {

    private String enderecoCarteira;
    private Short idMoeda;
    private BigDecimal saldo;
    private LocalDateTime dataAtualizacao;

    public SaldoCarteira() {}

    public SaldoCarteira(String enderecoCarteira, Short idMoeda, BigDecimal saldo) {
        this.enderecoCarteira = enderecoCarteira;
        this.idMoeda = idMoeda;
        this.saldo = saldo;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public String getEnderecoCarteira() { return enderecoCarteira; }
    public void setEnderecoCarteira(String enderecoCarteira) { this.enderecoCarteira = enderecoCarteira; }
    public Short getIdMoeda() { return idMoeda; }
    public void setIdMoeda(Short idMoeda) { this.idMoeda = idMoeda; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}