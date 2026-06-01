package com.projeto.carteiradigital.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transacao")
    private Long idTransacao;

    @ManyToOne
    @JoinColumn(name = "endereco_carteira", nullable = false)
    private Carteira carteira;

    @ManyToOne
    @JoinColumn(name = "id_moeda", nullable = false)
    private Moeda moeda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false)
    private TipoTransacao tipoOperacao;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Column(name = "taxa_cobrada", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxaCobrada;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected Transacao(){}

    public Transacao(Carteira carteira, Moeda moeda, TipoTransacao tipoOperacao, BigDecimal valor, BigDecimal taxaCobrada) {
        this.carteira = carteira;
        this.moeda = moeda;
        this.tipoOperacao = tipoOperacao;
        this.valor = valor;
        this.taxaCobrada = taxaCobrada;
        this.dataHora = LocalDateTime.now(); 
    }

    public Long getIdTransacao() { return idTransacao; }
    public Carteira getCarteira() { return carteira; }
    public Moeda getMoeda() { return moeda; }
    public TipoTransacao getTipoOperacao() { return tipoOperacao; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getTaxaCobrada() { return taxaCobrada; }
    public LocalDateTime getDataHora() { return dataHora; }
}