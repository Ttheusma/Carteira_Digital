package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.*;
import com.projeto.carteiradigital.repository.ConversaoRepository;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import com.projeto.carteiradigital.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConversaoService {

    private final ConversaoRepository conversaoRepository;
    private final TransacaoRepository transacaoRepository;
    private final SaldoCarteiraRepository saldoCarteiraRepository;
    private final CarteiraService carteiraService;
    private final MoedaService moedaService;

    // Blindagem de SecOps: Taxa padrão de 2% (0.02) caso o Docker falhe
    @Value("${APP_TAXA_CONVERSAO:0.02}")
    private BigDecimal taxaConversaoPercentual;

    public ConversaoService(ConversaoRepository conversaoRepository,
                            TransacaoRepository transacaoRepository,
                            SaldoCarteiraRepository saldoCarteiraRepository,
                            CarteiraService carteiraService,
                            MoedaService moedaService) {
        this.conversaoRepository = conversaoRepository;
        this.transacaoRepository = transacaoRepository;
        this.saldoCarteiraRepository = saldoCarteiraRepository;
        this.carteiraService = carteiraService;
        this.moedaService = moedaService;
    }

    @Transactional
    public Conversao converter(String endereco, String codigoOrigem, String codigoDestino, BigDecimal valor, BigDecimal cotacao) {
        
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || cotacao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alerta SecOps: Valor e cotação devem ser maiores que zero.");
        }
        if (codigoOrigem.equalsIgnoreCase(codigoDestino)) {
            throw new IllegalArgumentException("Alerta SecOps: As moedas de origem e destino devem ser diferentes.");
        }

        
        Carteira carteira = carteiraService.buscarCarteiraSegura(endereco);
        Moeda moedaOrigem = moedaService.buscarPorCodigoSeguro(codigoOrigem);
        Moeda moedaDestino = moedaService.buscarPorCodigoSeguro(codigoDestino);

        SaldoCarteira saldoOrigem = saldoCarteiraRepository
                .findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, codigoOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Operação Negada: Saldo insuficiente na moeda de origem."));

        if (saldoOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Operação Negada: Saldo insuficiente para realizar a conversão.");
        }

        
        BigDecimal valorDestinoBruto = valor.multiply(cotacao).setScale(8, RoundingMode.HALF_UP);
        
        
        BigDecimal valorTaxa = valorDestinoBruto.multiply(taxaConversaoPercentual)
                .divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);

        
        BigDecimal valorDestinoLiquido = valorDestinoBruto.subtract(valorTaxa);

       
        SaldoCarteira saldoDestino = saldoCarteiraRepository
                .findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, codigoDestino)
                .orElse(new SaldoCarteira(carteira, moedaDestino, BigDecimal.ZERO));

        
        saldoOrigem.setSaldo(saldoOrigem.getSaldo().subtract(valor));
        saldoDestino.setSaldo(saldoDestino.getSaldo().add(valorDestinoLiquido));

        saldoCarteiraRepository.save(saldoOrigem);
        saldoCarteiraRepository.save(saldoDestino);

        
        Conversao conversao = new Conversao(carteira, moedaOrigem, moedaDestino, valor, valorDestinoBruto, cotacao, taxaConversaoPercentual, valorTaxa);
        conversaoRepository.save(conversao);

        
        Transacao transacaoSaida = new Transacao(carteira, moedaOrigem, TipoTransacao.CONVERSAO_SAIDA, valor, BigDecimal.ZERO);
        transacaoRepository.save(transacaoSaida);

        Transacao transacaoEntrada = new Transacao(carteira, moedaDestino, TipoTransacao.CONVERSAO_ENTRADA, valorDestinoLiquido, valorTaxa);
        transacaoRepository.save(transacaoEntrada);

        return conversao;
    }
}