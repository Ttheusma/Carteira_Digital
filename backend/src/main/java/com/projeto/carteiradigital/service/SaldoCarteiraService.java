package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaldoCarteiraService {

    private final SaldoCarteiraRepository saldoCarteiraRepository;

    public SaldoCarteiraService(SaldoCarteiraRepository saldoCarteiraRepository) {
        this.saldoCarteiraRepository = saldoCarteiraRepository;
    }

    public List<SaldoCarteira> listarSaldos(String enderecoCarteira) {
        return saldoCarteiraRepository.findAllByEnderecoCarteira(enderecoCarteira);
    }

    public SaldoCarteira obterSaldo(String enderecoCarteira, Short idMoeda) {
        return saldoCarteiraRepository.findById(enderecoCarteira, idMoeda)
                .orElse(new SaldoCarteira(enderecoCarteira, idMoeda, BigDecimal.ZERO));
    }

    private void atualizarSaldo(String enderecoCarteira, Short idMoeda, BigDecimal novoValor) {
        SaldoCarteira saldo = new SaldoCarteira(enderecoCarteira, idMoeda, novoValor);
        saldoCarteiraRepository.saveOrUpdate(saldo);
    }
    
    public void adicionarSaldo(String enderecoCarteira, Short idMoeda, BigDecimal valorAdicional) {
        SaldoCarteira saldoAtual = obterSaldo(enderecoCarteira, idMoeda);
        BigDecimal novoValor = saldoAtual.getSaldo().add(valorAdicional);
        atualizarSaldo(enderecoCarteira, idMoeda, novoValor);
    }

    public void subtrairSaldo(String enderecoCarteira, Short idMoeda, BigDecimal valorSubtracao) {
        SaldoCarteira saldoAtual = obterSaldo(enderecoCarteira, idMoeda);
        if (saldoAtual.getSaldo().compareTo(valorSubtracao) < 0) {
            throw new RuntimeException("Saldo insuficiente para cobrir o valor da operação e as taxas associadas.");
        }
        BigDecimal novoValor = saldoAtual.getSaldo().subtract(valorSubtracao);
        atualizarSaldo(enderecoCarteira, idMoeda, novoValor);
    }
}