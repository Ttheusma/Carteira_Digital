package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.repository.CarteiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;

    public CarteiraService(CarteiraRepository carteiraRepository) {
        this.carteiraRepository = carteiraRepository;
    }

    /**
     * @Transactional garante o conceito ACID do banco. 
     * Se der erro no meio do método, ele faz o Rollback automático de tudo.
     */
    @Transactional
    public Carteira criarNovaCarteira(String hashChavePrivada) {
      
        if (hashChavePrivada == null || hashChavePrivada.trim().isEmpty()) {
            throw new IllegalArgumentException("Falha de Segurança: O hash da chave privada é obrigatório.");
        }

       
        String novoEndereco = UUID.randomUUID().toString();

      
        Carteira novaCarteira = new Carteira(novoEndereco, hashChavePrivada, "ATIVA");

        
        return carteiraRepository.save(novaCarteira);
    }
    
    public Carteira buscarCarteiraSegura(String endereco) {
        return carteiraRepository.findById(endereco)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada para o endereço: " + endereco));
    }
}