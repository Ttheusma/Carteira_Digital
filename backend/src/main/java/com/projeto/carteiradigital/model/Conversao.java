package com.projeto.carteiradigital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Conversao {

    private Long idConversao;
    private String enderecoCarteira;
    private Short idMoedaOrigem;
    private Short idMoedaDestino;
    private BigDecimal valorOrigem;
    private BigDecimal valorDestino;
    private BigDecimal taxaPercentual;
    private BigDecimal taxaValor;
    private BigDecimal cotacaoUtilizada;
    private LocalDateTime dataHora;

    public Conversao() {}

    // Getters e Setters
    public Long getIdConversao() { return idConversao; }
    public void setIdConversao(Long idConversao) { this.idConversao = idConversao; }
    public String getEnderecoCarteira() { return enderecoCarteira; }
    public void setEnderecoCarteira(String enderecoCarteira) { this.enderecoCarteira = enderecoCarteira; }
    public Short getIdMoedaOrigem() { return idMoedaOrigem; }
    public void setIdMoedaOrigem(Short idMoedaOrigem) { this.idMoedaOrigem = idMoedaOrigem; }
    public Short getIdMoedaDestino() { return idMoedaDestino; }
    public void setIdMoedaDestino(Short idMoedaDestino) { this.idMoedaDestino = idMoedaDestino; }
    public BigDecimal getValorOrigem() { return valorOrigem; }
    public void setValorOrigem(BigDecimal valorOrigem) { this.valorOrigem = valorOrigem; }
    public BigDecimal getValorDestino() { return valorDestino; }
    public void setValorDestino(BigDecimal valorDestino) { this.valorDestino = valorDestino; }
    public BigDecimal getTaxaPercentual() { return taxaPercentual; }
    public void setTaxaPercentual(BigDecimal taxaPercentual) { this.taxaPercentual = taxaPercentual; }
    public BigDecimal getTaxaValor() { return taxaValor; }
    public void setTaxaValor(BigDecimal taxaValor) { this.taxaValor = taxaValor; }
    public BigDecimal getCotacaoUtilizada() { return cotacaoUtilizada; }
    public void setCotacaoUtilizada(BigDecimal cotacaoUtilizada) { this.cotacaoUtilizada = cotacaoUtilizada; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}