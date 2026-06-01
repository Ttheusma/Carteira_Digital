package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaldoCarteiraService {

    private final SaldoCarteiraRepository saldoCarteiraRepository;
    
    
    private final CarteiraService carteiraService;
    private final MoedaService moedaService;

    public SaldoCarteiraService(SaldoCarteiraRepository saldoCarteiraRepository,
                                CarteiraService carteiraService,
                                MoedaService moedaService) {
        this.saldoCarteiraRepository = saldoCarteiraRepository;
        this.carteiraService = carteiraService;
        this.moedaService = moedaService;
    }

   
    @Transactional
    public SaldoCarteira inicializarSaldo(String enderecoCarteira, String codigoMoeda) {
        
        Carteira carteira = carteiraService.buscarCarteiraSegura(enderecoCarteira);
        Moeda moeda = moedaService.buscarPorCodigoSeguro(codigoMoeda);

       
        SaldoCarteira novoSaldo = new SaldoCarteira(carteira, moeda, BigDecimal.ZERO);

       
        return saldoCarteiraRepository.save(novoSaldo);
    }

    
    public List<SaldoCarteira> buscarSaldosDaCarteira(String enderecoCarteira) {
       
        carteiraService.buscarCarteiraSegura(enderecoCarteira);
        
        return saldoCarteiraRepository.findByCarteira_EnderecoCarteira(enderecoCarteira);
    }
}