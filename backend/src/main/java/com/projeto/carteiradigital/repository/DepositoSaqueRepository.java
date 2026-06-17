package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.DepositoSaque;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DepositoSaqueRepository {

    private final JdbcTemplate jdbcTemplate;

    public DepositoSaqueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(DepositoSaque movimento) {
        String sql = "INSERT INTO DEPOSITO_SAQUE (endereco_carteira, id_moeda, tipo, valor, taxa_valor, data_hora) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        jdbcTemplate.update(sql,
                movimento.getEnderecoCarteira(),
                movimento.getIdMoeda(),
                movimento.getTipo(),
                movimento.getValor(),
                movimento.getTaxaValor());
    }
}