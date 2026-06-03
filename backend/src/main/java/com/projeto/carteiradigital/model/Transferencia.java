package com.projeto.carteiradigital.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "transferencia")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transferencia")
    private Long idTransferencia;

    // Relacionamento com a carteira que ENVIOU o dinheiro
    @ManyToOne
    @JoinColumn(name = "endereco_origem", nullable = false)
    private Carteira carteiraOrigem;

    // Relacionamento com a carteira que RECEBEU o dinheiro
    @ManyToOne
    @JoinColumn(name = "endereco_destino", nullable = false)
    private Carteira carteiraDestino;

    @ManyToOne
    @JoinColumn(name = "id_moeda", nullable = false)
    private Moeda moeda;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal valor;

    @Column(name = "taxa_valor", nullable = false, precision = 20, scale = 8)
    private BigDecimal taxaValor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Construtor vazio exigido pelo Hibernate
    protected Transferencia() {}

    // Construtor seguro para o nosso Service usar
    public Transferencia(Carteira carteiraOrigem, Carteira carteiraDestino, Moeda moeda, BigDecimal valor, BigDecimal taxaValor) {
        this.carteiraOrigem = carteiraOrigem;
        this.carteiraDestino = carteiraDestino;
        this.moeda = moeda;
        this.valor = valor;
        this.taxaValor = taxaValor;
        this.dataHora = LocalDateTime.now();
    }

    // Getters
    public Long getIdTransferencia() { return idTransferencia; }
    public Carteira getCarteiraOrigem() { return carteiraOrigem; }
    public Carteira getCarteiraDestino() { return carteiraDestino; }
    public Moeda getMoeda() { return moeda; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getTaxaValor() { return taxaValor; }
    public LocalDateTime getDataHora() { return dataHora; }
}