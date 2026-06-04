package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.repository.CarteiraRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;

    public CarteiraService(CarteiraRepository carteiraRepository) {
        this.carteiraRepository = carteiraRepository;
    }

    // 1. Método usado pelo CarteiraController (A sua versão nova)
    public Carteira criarCarteira(String enderecoCarteira, String hashChavePrivada) {
        Carteira novaCarteira = new Carteira(enderecoCarteira, hashChavePrivada, "ATIVA");
        return carteiraRepository.save(novaCarteira);
    }

    // 2. Método usado pelo TransacaoController (A sua versão nova)
    public Optional<Carteira> buscarPorEndereco(String enderecoCarteira) {
        return carteiraRepository.findById(enderecoCarteira);
    }

    // 3. MÉTODO CRÍTICO usado pelos outros Services (Para consertar a compilação)
    public Carteira buscarCarteiraSegura(String endereco) {
        return carteiraRepository.findById(endereco)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada para o endereço: " + endereco));
    }
}