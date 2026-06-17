package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.exception.AcessoNegadoException;
import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.repository.CarteiraRepository;
import com.projeto.carteiradigital.util.CryptoUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;
    private final CryptoUtils cryptoUtils;

    public CarteiraService(CarteiraRepository carteiraRepository, CryptoUtils cryptoUtils) {
        this.carteiraRepository = carteiraRepository;
        this.cryptoUtils = cryptoUtils;
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

    // ================================================================
    // MOTOR DE SEGURANÇA — usado nas operações de saída (saque, transferência, conversão)
    // Garante que a carteira existe E que a chave privada corresponde.
    // ================================================================
    public Carteira validarAcesso(String enderecoCarteira, String chavePrivadaFornecida) {
        if (chavePrivadaFornecida == null || chavePrivadaFornecida.isBlank()) {
            throw new AcessoNegadoException("Acesso Negado: chave privada é obrigatória.");
        }

        Carteira carteira = carteiraRepository.findById(enderecoCarteira)
                .orElseThrow(() -> new AcessoNegadoException("Acesso Negado: carteira ou chave inválida."));

        String hashFornecido = cryptoUtils.gerarHashSHA256(chavePrivadaFornecida);

        if (!carteira.getHashChavePrivada().equals(hashFornecido)) {
            throw new AcessoNegadoException("Acesso Negado: carteira ou chave inválida.");
        }

        return carteira;
    }

    // ================================================================
    // LOGIN — usado apenas pela tela de acesso.
    // O usuário fornece o ENDEREÇO da carteira (chave pública/de acesso).
    // A chave privada (maior) é exigida só nas operações (saque, câmbio, transferência).
    // ================================================================
    public Carteira validarAcessoPorEndereco(String enderecoCarteira) {
        if (enderecoCarteira == null || enderecoCarteira.isBlank()) {
            throw new AcessoNegadoException("Acesso Negado: endereço da carteira é obrigatório.");
        }

        return carteiraRepository.findById(enderecoCarteira)
                .orElseThrow(() -> new AcessoNegadoException("Acesso Negado: endereço inválido."));
    }
}