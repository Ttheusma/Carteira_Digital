package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Moeda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoedaRepository extends JpaRepository<Moeda, Short> {
    
    
    Optional<Moeda> findByCodigo(String codigo);
}