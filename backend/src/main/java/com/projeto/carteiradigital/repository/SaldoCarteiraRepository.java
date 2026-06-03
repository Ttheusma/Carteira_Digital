package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.model.SaldoCarteiraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaldoCarteiraRepository extends JpaRepository<SaldoCarteira, SaldoCarteiraId> {

    
    List<SaldoCarteira> findByCarteira_EnderecoCarteira(String enderecoCarteira);

    
    Optional<SaldoCarteira> findByCarteira_EnderecoCarteiraAndMoeda_Codigo(String enderecoCarteira, String codigo);
    
}