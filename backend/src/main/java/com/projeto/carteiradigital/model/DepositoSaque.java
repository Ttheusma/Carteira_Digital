package com.projeto.carteiradigital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositoSaque {

    private Long idMovimento;
    private String enderecoCarteira;
    private Short idMoeda;
    private String tipo; // 'DEPOSITO' ou 'SAQUE'
    private BigDecimal valor;
    private BigDecimal taxaValor;
    private LocalDateTime dataHora;

    public DepositoSaque() {}

    // Getters e Setters
    public Long getIdMovimento() { return idMovimento; }
    public void setIdMovimento(Long idMovimento) { this.idMovimento = idMovimento; }
    public String getEnderecoCarteira() { return enderecoCarteira; }
    public void setEnderecoCarteira(String enderecoCarteira) { this.enderecoCarteira = enderecoCarteira; }
    public Short getIdMoeda() { return idMoeda; }
    public void setIdMoeda(Short idMoeda) { this.idMoeda = idMoeda; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getTaxaValor() { return taxaValor; }
    public void setTaxaValor(BigDecimal taxaValor) { this.taxaValor = taxaValor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}