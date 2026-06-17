package com.projeto.carteiradigital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transferencia {

    private Long idTransferencia;
    private String enderecoOrigem;
    private String enderecoDestino;
    private Short idMoeda;
    private BigDecimal valor;
    private BigDecimal taxaValor;
    private LocalDateTime dataHora;

    public Transferencia() {}

    // Getters e Setters
    public Long getIdTransferencia() { return idTransferencia; }
    public void setIdTransferencia(Long idTransferencia) { this.idTransferencia = idTransferencia; }
    public String getEnderecoOrigem() { return enderecoOrigem; }
    public void setEnderecoOrigem(String enderecoOrigem) { this.enderecoOrigem = enderecoOrigem; }
    public String getEnderecoDestino() { return enderecoDestino; }
    public void setEnderecoDestino(String enderecoDestino) { this.enderecoDestino = enderecoDestino; }
    public Short getIdMoeda() { return idMoeda; }
    public void setIdMoeda(Short idMoeda) { this.idMoeda = idMoeda; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getTaxaValor() { return taxaValor; }
    public void setTaxaValor(BigDecimal taxaValor) { this.taxaValor = taxaValor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}