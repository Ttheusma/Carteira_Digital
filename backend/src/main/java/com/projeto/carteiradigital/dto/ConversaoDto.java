package com.projeto.carteiradigital.dto;
import java.math.BigDecimal;

public class ConversaoDto {
    private String moedaOrigem;
    private String moedaDestino;
    private BigDecimal valorOrigem;
    private String chavePrivada;

    public String getMoedaOrigem() { return moedaOrigem; }
    public void setMoedaOrigem(String moedaOrigem) { this.moedaOrigem = moedaOrigem; }
    public String getMoedaDestino() { return moedaDestino; }
    public void setMoedaDestino(String moedaDestino) { this.moedaDestino = moedaDestino; }
    public BigDecimal getValorOrigem() { return valorOrigem; }
    public void setValorOrigem(BigDecimal valorOrigem) { this.valorOrigem = valorOrigem; }
    public String getChavePrivada() { return chavePrivada; }
    public void setChavePrivada(String chavePrivada) { this.chavePrivada = chavePrivada; }
}