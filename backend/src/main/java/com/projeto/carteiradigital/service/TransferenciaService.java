package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.*;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import com.projeto.carteiradigital.repository.TransacaoRepository;
import com.projeto.carteiradigital.repository.TransferenciaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final TransacaoRepository transacaoRepository;
    private final SaldoCarteiraRepository saldoCarteiraRepository;
    private final CarteiraService carteiraService;
    private final MoedaService moedaService;

    @Value("${APP_TAXA_TRANSFERENCIA:0.01}")
    private BigDecimal taxaTransferenciaPercentual;

    public TransferenciaService(TransferenciaRepository transferenciaRepository,
                                TransacaoRepository transacaoRepository,
                                SaldoCarteiraRepository saldoCarteiraRepository,
                                CarteiraService carteiraService,
                                MoedaService moedaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.transacaoRepository = transacaoRepository;
        this.saldoCarteiraRepository = saldoCarteiraRepository;
        this.carteiraService = carteiraService;
        this.moedaService = moedaService;
    }

    @Transactional
    public Transferencia transferir(String enderecoOrigem, String enderecoDestino, String codigoMoeda, BigDecimal valor) {
       
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alerta SecOps: O valor da transferência deve ser maior que zero.");
        }

       
        if (enderecoOrigem.equals(enderecoDestino)) {
            throw new IllegalArgumentException("Alerta SecOps: Não é permitido transferir fundos para a própria carteira.");
        }

        
        Carteira origem = carteiraService.buscarCarteiraSegura(enderecoOrigem);
        Carteira destino = carteiraService.buscarCarteiraSegura(enderecoDestino);
        Moeda moeda = moedaService.buscarPorCodigoSeguro(codigoMoeda);

        SaldoCarteira saldoOrigem = saldoCarteiraRepository
                .findByCarteira_EnderecoCarteiraAndMoeda_Codigo(enderecoOrigem, codigoMoeda)
                .orElseThrow(() -> new IllegalArgumentException("Operação Negada: Carteira de origem não possui saldo nesta moeda."));

        
        BigDecimal valorTaxa = valor.multiply(taxaTransferenciaPercentual)
                .divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
        BigDecimal totalDesconto = valor.add(valorTaxa);

        
        if (saldoOrigem.getSaldo().compareTo(totalDesconto) < 0) {
            throw new IllegalArgumentException("Operação Negada: Saldo insuficiente para cobrir a transferência e as taxas.");
        }

  
        SaldoCarteira saldoDestino = saldoCarteiraRepository
                .findByCarteira_EnderecoCarteiraAndMoeda_Codigo(enderecoDestino, codigoMoeda)
                .orElse(new SaldoCarteira(destino, moeda, BigDecimal.ZERO));

        
        saldoOrigem.setSaldo(saldoOrigem.getSaldo().subtract(totalDesconto));
        saldoDestino.setSaldo(saldoDestino.getSaldo().add(valor));

        saldoCarteiraRepository.save(saldoOrigem);
        saldoCarteiraRepository.save(saldoDestino);

        
        Transferencia transferencia = new Transferencia(origem, destino, moeda, valor, valorTaxa);
        transferenciaRepository.save(transferencia);

        
        Transacao transacaoSaida = new Transacao(origem, moeda, TipoTransacao.TRANSFERENCIA_ENVIADA, valor, valorTaxa);
        transacaoRepository.save(transacaoSaida);
        
        Transacao transacaoEntrada = new Transacao(destino, moeda, TipoTransacao.TRANSFERENCIA_RECEBIDA, valor, BigDecimal.ZERO);
        transacaoRepository.save(transacaoEntrada);

        return transferencia;
    }
}