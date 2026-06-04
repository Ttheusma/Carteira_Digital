package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.repository.MoedaRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MoedaService {

    private final MoedaRepository moedaRepository;

    public MoedaService(MoedaRepository moedaRepository) {
        this.moedaRepository = moedaRepository;
    }

    public Optional<Moeda> buscarPorCodigo(String codigo) {
        return moedaRepository.findByCodigo(codigo);
    }
}