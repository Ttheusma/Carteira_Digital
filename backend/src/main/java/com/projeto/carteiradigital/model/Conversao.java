package com.projeto.carteiradigital.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversao")
public class Conversao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversao")
    private Long idConversao;

   
    @ManyToOne
    @JoinColumn(name = "endereco_carteira", nullable = false)
    private Carteira carteira;

    @ManyToOne
    @JoinColumn(name = "id_moeda_origem", nullable = false)
    private Moeda moedaOrigem;

    @ManyToOne
    @JoinColumn(name = "id_moeda_destino", nullable = false)
    private Moeda moedaDestino;

    
    @Column(name = "valor_origem", nullable = false, precision = 20, scale = 8)
    private BigDecimal valorOrigem;

    @Column(name = "valor_destino", nullable = false, precision = 20, scale = 8)
    private BigDecimal valorDestino;

    @Column(name = "cotacao_utilizada", nullable = false, precision = 20, scale = 8)
    private BigDecimal cotacaoUtilizada;

    @Column(name = "taxa_percentual", nullable = false, precision = 20, scale = 8)
    private BigDecimal taxaPercentual;

    @Column(name = "taxa_valor", nullable = false, precision = 20, scale = 8)
    private BigDecimal taxaValor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    
    protected Conversao() {}

    
    public Conversao(Carteira carteira, Moeda moedaOrigem, Moeda moedaDestino, BigDecimal valorOrigem, 
                     BigDecimal valorDestino, BigDecimal cotacaoUtilizada, BigDecimal taxaPercentual, BigDecimal taxaValor) {
        this.carteira = carteira;
        this.moedaOrigem = moedaOrigem;
        this.moedaDestino = moedaDestino;
        this.valorOrigem = valorOrigem;
        this.valorDestino = valorDestino;
        this.cotacaoUtilizada = cotacaoUtilizada;
        this.taxaPercentual = taxaPercentual;
        this.taxaValor = taxaValor;
        this.dataHora = LocalDateTime.now();
    }

    
    public Long getIdConversao() { return idConversao; }
    public Carteira getCarteira() { return carteira; }
    public Moeda getMoedaOrigem() { return moedaOrigem; }
    public Moeda getMoedaDestino() { return moedaDestino; }
    public BigDecimal getValorOrigem() { return valorOrigem; }
    public BigDecimal getValorDestino() { return valorDestino; }
    public BigDecimal getCotacaoUtilizada() { return cotacaoUtilizada; }
    public BigDecimal getTaxaPercentual() { return taxaPercentual; }
    public BigDecimal getTaxaValor() { return taxaValor; }
    public LocalDateTime getDataHora() { return dataHora; }
}