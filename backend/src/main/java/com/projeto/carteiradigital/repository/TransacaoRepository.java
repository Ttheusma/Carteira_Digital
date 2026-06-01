package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
   
    List<Transacao> findByCarteira_EnderecoCarteiraOrderByDataHoraDesc(String enderecoCarteira);
}