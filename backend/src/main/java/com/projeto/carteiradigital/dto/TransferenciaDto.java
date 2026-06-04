package com.projeto.carteiradigital.dto;
import java.math.BigDecimal;

public class TransferenciaDto {
    private String enderecoDestino;
    private String codigoMoeda;
    private BigDecimal valor;
    private String chavePrivada;

    public String getEnderecoDestino() { return enderecoDestino; }
    public void setEnderecoDestino(String enderecoDestino) { this.enderecoDestino = enderecoDestino; }
    public String getCodigoMoeda() { return codigoMoeda; }
    public void setCodigoMoeda(String codigoMoeda) { this.codigoMoeda = codigoMoeda; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getChavePrivada() { return chavePrivada; }
    public void setChavePrivada(String chavePrivada) { this.chavePrivada = chavePrivada; }
}