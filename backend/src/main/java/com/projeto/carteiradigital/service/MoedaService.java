package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.repository.MoedaRepository;
import org.springframework.stereotype.Service;

@Service
public class MoedaService {

    private final MoedaRepository moedaRepository;

    
    public MoedaService(MoedaRepository moedaRepository) {
        this.moedaRepository = moedaRepository;
    }

    
    public Moeda buscarPorCodigoSeguro(String codigo) {
        return moedaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Alerta de Negócio: Moeda não suportada ou código inválido: " + codigo));
    }
}