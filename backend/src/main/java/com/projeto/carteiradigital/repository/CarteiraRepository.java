package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, String> {
}