package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.model.TipoTransacao;
import com.projeto.carteiradigital.model.Transacao;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import com.projeto.carteiradigital.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final SaldoCarteiraRepository saldoCarteiraRepository;
    private final CarteiraService carteiraService;
    private final MoedaService moedaService;
    @Value("${APP_TAXA_SAQUE:0.01}")
    private BigDecimal taxaSaquePercentual;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            SaldoCarteiraRepository saldoCarteiraRepository,
                            CarteiraService carteiraService,
                            MoedaService moedaService) {
        this.transacaoRepository = transacaoRepository;
        this.saldoCarteiraRepository = saldoCarteiraRepository;
        this.carteiraService = carteiraService;
        this.moedaService = moedaService;
    }


    @Transactional
    public Transacao depositar(String endereco, String codigoMoeda, BigDecimal valor) {
        // Fail-Fast: Bloqueia valores negativos ou zerados instantaneamente
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alerta de Segurança: O valor do depósito deve ser maior que zero.");
        }

        Carteira carteira = carteiraService.buscarCarteiraSegura(endereco);
        Moeda moeda = moedaService.buscarPorCodigoSeguro(codigoMoeda);

        // Busca o saldo atual. Se não existir (primeiro depósito na moeda), inicia com ZERO.
        SaldoCarteira saldo = saldoCarteiraRepository
                .findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, codigoMoeda)
                .orElse(new SaldoCarteira(carteira, moeda, BigDecimal.ZERO));

        // Adiciona o valor depositado usando a precisão absoluta do BigDecimal
        saldo.setSaldo(saldo.getSaldo().add(valor));
        saldoCarteiraRepository.save(saldo);

        // Registra o Rastro de Auditoria (Extrato)
        Transacao transacao = new Transacao(carteira, moeda, TipoTransacao.DEPOSITO, valor, BigDecimal.ZERO);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao sacar(String endereco, String codigoMoeda, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alerta de Segurança: O valor do saque deve ser maior que zero.");
        }

        Carteira carteira = carteiraService.buscarCarteiraSegura(endereco);
        Moeda moeda = moedaService.buscarPorCodigoSeguro(codigoMoeda);

        SaldoCarteira saldo = saldoCarteiraRepository
                .findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, codigoMoeda)
                .orElseThrow(() -> new IllegalArgumentException("Operação Negada: Carteira não possui saldo nesta moeda."));

        // Cálculos Financeiros Seguros: (Valor * Taxa / 100)
        // O RoundingMode.HALF_UP garante o arredondamento correto de centavos (ex: R$ 0,005 vira R$ 0,01)
        BigDecimal valorTaxa = valor.multiply(taxaSaquePercentual)
                                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        
        BigDecimal totalDesconto = valor.add(valorTaxa);

        // Validação estrita de saldo contra overdraft
        if (saldo.getSaldo().compareTo(totalDesconto) < 0) {
            throw new IllegalArgumentException("Operação Negada: Saldo insuficiente para cobrir o saque e as taxas.");
        }

        // Subtrai do saldo
        saldo.setSaldo(saldo.getSaldo().subtract(totalDesconto));
        saldoCarteiraRepository.save(saldo);

        // Grava a auditoria física da transação e da taxa cobrada
        Transacao transacao = new Transacao(carteira, moeda, TipoTransacao.SAQUE, valor, valorTaxa);
        return transacaoRepository.save(transacao);
    }




}