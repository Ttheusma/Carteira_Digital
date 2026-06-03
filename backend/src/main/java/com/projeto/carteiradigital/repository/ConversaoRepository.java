package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Conversao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversaoRepository extends JpaRepository<Conversao, Long> {
}