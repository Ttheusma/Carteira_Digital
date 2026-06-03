package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.model.Transacao;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import com.projeto.carteiradigital.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

@Transactional(readOnly = true) 
public class ConsultaService {

    private final SaldoCarteiraRepository saldoCarteiraRepository;
    private final TransacaoRepository transacaoRepository;
    private final CarteiraService carteiraService;

    public ConsultaService(SaldoCarteiraRepository saldoCarteiraRepository,
                           TransacaoRepository transacaoRepository,
                           CarteiraService carteiraService) {
        this.saldoCarteiraRepository = saldoCarteiraRepository;
        this.transacaoRepository = transacaoRepository;
        this.carteiraService = carteiraService;
    }

    public List<SaldoCarteira> consultarSaldos(String endereco) {
       
        carteiraService.buscarCarteiraSegura(endereco);
        return saldoCarteiraRepository.findByCarteira_EnderecoCarteira(endereco);
    }

    public List<Transacao> consultarExtrato(String endereco) {
        
        carteiraService.buscarCarteiraSegura(endereco);
        return transacaoRepository.findByCarteira_EnderecoCarteiraOrderByDataHoraDesc(endereco);
    }
}